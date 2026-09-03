package com.takamasafukase.ar_gunman_android.features.game

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ar_gunman_android.device.arShootingEngine.ARShootingEngineHandlerInterface
import com.ar_gunman_android.device.motionSensor.MotionSensorHandlerInterface
import com.ar_gunman_android.device.sound.SoundPlayerInterface
import com.ar_gunman_android.device.sound.SoundType
import com.ar_gunman_android.domain.entities.game.GameFlowStatus
import com.ar_gunman_android.domain.entities.game.ReloadingMotionDetectedCountUpdateResult
import com.ar_gunman_android.domain.entities.motion.WeaponControlMotion
import com.ar_gunman_android.domain.entities.weapon.WeaponFireResult
import com.ar_gunman_android.domain.entities.weapon.WeaponReloadStartResult
import com.ar_gunman_android.domain.entities.weapon.WeaponType
import com.ar_gunman_android.domain.storeInterfaces.GameStoreInterface
import com.ar_gunman_android.domain.storeInterfaces.WeaponStoreInterface
import com.ar_gunman_android.domain.useCases.GameFlowDriveUseCaseInterface
import com.ar_gunman_android.domain.useCases.ReloadingMotionCountUpdateUseCaseInterface
import com.ar_gunman_android.domain.useCases.ScoreAddUseCaseInterface
import com.ar_gunman_android.domain.useCases.WeaponChangeUseCaseInterface
import com.ar_gunman_android.domain.useCases.WeaponControlMotionDetectUseCaseInterface
import com.ar_gunman_android.domain.useCases.WeaponFireUseCaseInterface
import com.ar_gunman_android.domain.useCases.WeaponReloadUseCaseInterface
import com.takamasafukase.ar_gunman_android.constants.SavedStateHandleKeys
import com.takamasafukase.ar_gunman_android.extensions.timeCountText
import com.takamasafukase.ar_gunman_android.features.game.weaponResources.soundResources
import com.takamasafukase.ar_gunman_android.features.game.weaponResources.uiResources
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class GameViewModel(
    savedStateHandle: SavedStateHandle,
    private val arShootingEngineHandler: ARShootingEngineHandlerInterface,
    private val motionSensorHandler: MotionSensorHandlerInterface,
    private val soundPlayer: SoundPlayerInterface,
    private val gameStore: GameStoreInterface,
    private val weaponStore: WeaponStoreInterface,
    private val weaponFireUseCase: WeaponFireUseCaseInterface,
    private val weaponReloadUseCase: WeaponReloadUseCaseInterface,
    private val weaponChangeUseCase: WeaponChangeUseCaseInterface,
    private val gameFlowDriveUseCase: GameFlowDriveUseCaseInterface,
    private val scoreAddUseCase: ScoreAddUseCaseInterface,
    private val reloadingMotionCountUpdateUseCase: ReloadingMotionCountUpdateUseCaseInterface,
    private val weaponControlMotionDetectUseCase: WeaponControlMotionDetectUseCaseInterface,
) : ViewModel() {
    data class UIState(
        val timeCountText: String = "",
        val currentWeaponType: WeaponType = WeaponType.defaultType,
        val sightImageId: Int = WeaponType.defaultType.uiResources.sightImageId,
        val bulletsCountImageName: String = "",
        val isWeaponChangeButtonEnabled: Boolean = false,
    )
    sealed interface OutputEventType {
        object ShowTutorialView : OutputEventType
        object ShowWeaponSelectView : OutputEventType
        object CloseWeaponSelectView : OutputEventType
        data class ShowResultView(val score: Double) : OutputEventType
    }

    val uiState: StateFlow<UIState> = combine(
        gameStore.gameFlow,
        gameStore.timeCount,
        weaponStore.weapon,
    ) { gameFlow, timeCount, weapon ->
        UIState(
            timeCountText = timeCount.countMillisec.timeCountText,
            currentWeaponType = weapon.currentType,
            sightImageId = weapon.currentType.uiResources.sightImageId,
            bulletsCountImageName = weapon.currentType.uiResources.bulletsCountImageName(
                bulletsCount = weapon.bulletsCount
            ),
            isWeaponChangeButtonEnabled = gameFlow.status.isTimerRunning,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
        initialValue = UIState(),
    )
    val outputEvent get() = _outputEvent.asSharedFlow()
    private val _outputEvent = MutableSharedFlow<OutputEventType>()

    init {
        viewModelScope.launch {
            savedStateHandle
                .getStateFlow(SavedStateHandleKeys.TUTORIAL_ENDED_EVENT, Unit)
                .drop(1)
                .collect {
                    tutorialEnded()
                }
        }

        viewModelScope.launch {
            savedStateHandle
                .getStateFlow(SavedStateHandleKeys.SELECTED_WEAPON_TYPE, WeaponType.defaultType)
                .drop(1)
                .collect { weaponType ->
                    weaponSelected(weaponType)
                }
        }

        arShootingEngineHandler.targetHit = { weaponType ->
            scoreAddUseCase.execute(targetHitPoint = weaponType.targetHitPoint)
            soundPlayer.play(SoundType.TARGET_HIT)
            weaponType.soundResources.bulletHitSound?.let {
                soundPlayer.play(it)
            }
        }

        motionSensorHandler.motionUpdated = motionUpdated@{ motion ->
            // 物理モーションを武器の操作モーションに変換
            val weaponControlMotion = weaponControlMotionDetectUseCase.execute(motion = motion)

            // 武器の操作モーションでは無い場合はreturn
            weaponControlMotion ?: return@motionUpdated

            // 武器の操作モーション種別をハンドリング
            when (weaponControlMotion) {
                WeaponControlMotion.FIRE -> {
                    // 武器の発射
                    viewModelScope.launch {
                        weaponFireUseCase.execute()
                    }
                }

                WeaponControlMotion.RELOAD -> {
                    // 武器のリロード
                    viewModelScope.launch {
                        weaponReloadUseCase.execute()
                    }

                    // リロードモーションの検知回数をカウント
                    val reloadingMotionCountUpdateResult =
                        reloadingMotionCountUpdateUseCase.execute()

                    // リロードモーションの検知回数に応じた結果のハンドリング
                    when (reloadingMotionCountUpdateResult) {
                        ReloadingMotionDetectedCountUpdateResult.NOT_EXCEEDED_LIMIT -> {}
                        ReloadingMotionDetectedCountUpdateResult.EXCEEDED_LIMIT -> {
                            soundPlayer.play(SoundType.TARGET_APPEARANCE_CHANGE)
                            arShootingEngineHandler.changeTargetsAppearance()
                        }
                    }
                }
            }
        }

        viewModelScope.launch {
            weaponFireUseCase.fireResultEvent
                .collect { fireResult ->
                    // 発射結果のハンドリング
                    when (fireResult) {
                        is WeaponFireResult.Success -> {
                            arShootingEngineHandler.renderWeaponFiring()
                            soundPlayer.play(weaponStore.weapon.value.currentType.soundResources.firingSound)
                        }

                        is WeaponFireResult.Failure -> {
                            when (fireResult.reason) {
                                WeaponFireResult.FailureReason.RELOADING -> {}
                                WeaponFireResult.FailureReason.OUT_OF_BULLETS -> {
                                    weaponStore.weapon.value.currentType.soundResources.outOfBulletsSound?.let { outOfBulletsSound ->
                                        soundPlayer.play(outOfBulletsSound)
                                    }
                                }
                            }
                        }
                    }
                }
        }

        viewModelScope.launch {
            weaponReloadUseCase.reloadStartResultEvent
                .collect { reloadStartResult ->
                    // リロード開始結果のハンドリング
                    when (reloadStartResult) {
                        WeaponReloadStartResult.SUCCESS -> {
                            soundPlayer.play(weaponStore.weapon.value.currentType.soundResources.reloadingSound)
                        }

                        WeaponReloadStartResult.FAILURE -> {}
                    }
                }
        }

        viewModelScope.launch {
            gameFlowDriveUseCase.statusStream
                .collect { status ->
                    when (status) {
                        GameFlowStatus.WaitingForTimerStart -> {
                            soundPlayer.play(WeaponType.defaultType.soundResources.appearingSound)
                        }

                        GameFlowStatus.TimerStartedAndWaitingForTimerEnd -> {
                            soundPlayer.play(SoundType.START_WHISTLE)
                            motionSensorHandler.startDetection()
                        }

                        GameFlowStatus.TimerEndedAndWaitingForFlowEnd -> {
                            soundPlayer.play(SoundType.END_WHISTLE)
                            motionSensorHandler.stopDetection()
                            viewModelScope.launch {
                                _outputEvent.emit(OutputEventType.CloseWeaponSelectView)
                            }
                        }

                        GameFlowStatus.FlowEnded -> {
                            soundPlayer.play(SoundType.RANKING_APPEAR)
                            viewModelScope.launch {
                                // 結果画面で表示する得点と一緒に遷移指示を流す
                                _outputEvent.emit(
                                    OutputEventType.ShowResultView(gameStore.score.value.value)
                                )
                            }
                        }

                        is GameFlowStatus.Blocked -> {
                            when (status.reason) {
                                GameFlowStatus.BlockedReason.TUTORIAL_NOT_COMPLETED -> {
                                    viewModelScope.launch {
                                        _outputEvent.emit(OutputEventType.ShowTutorialView)
                                    }
                                }

                                GameFlowStatus.BlockedReason.TIMER_PAUSED -> {}
                            }
                        }

                        GameFlowStatus.FlowNotStarted -> {}
                        GameFlowStatus.TimerResumedAndWaitingForTimerEnd -> {}
                        GameFlowStatus.CheckingTutorialCompletedStatus -> {}
                    }
                }
        }
    }

    fun onViewAppear() {
        gameStore.reset()
        weaponStore.reset()

        arShootingEngineHandler.run()
        arShootingEngineHandler.showWeapon(type = WeaponType.defaultType)
        viewModelScope.launch {
            gameFlowDriveUseCase.start()
        }
    }

    fun onViewDisappear() {
        arShootingEngineHandler.pause()
    }

    fun weaponChangeButtonTapped() {
        viewModelScope.launch {
            _outputEvent.emit(OutputEventType.ShowWeaponSelectView)

            // 武器選択中はタイムカウントの更新を止める
            gameFlowDriveUseCase.pauseTimer()
        }
    }

    // MARK: - Private Methods
    private fun tutorialEnded() {
        viewModelScope.launch {
            gameFlowDriveUseCase.resolveBlocked()
        }
    }

    private fun weaponSelected(weaponType: WeaponType) {
        weaponChangeUseCase.execute(newType = weaponType)
        arShootingEngineHandler.showWeapon(type = weaponType)
        soundPlayer.play(weaponType.soundResources.appearingSound)

        // タイムカウントの更新を再開する
        viewModelScope.launch {
            gameFlowDriveUseCase.resolveBlocked()
        }
    }
}

