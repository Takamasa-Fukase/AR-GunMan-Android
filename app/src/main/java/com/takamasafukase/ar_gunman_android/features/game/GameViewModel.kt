package com.takamasafukase.ar_gunman_android.features.game

import android.hardware.SensorManager
import android.os.Handler
import android.os.Looper
import android.util.Log
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
import com.takamasafukase.ar_gunman_android.model.AndroidToUnityMessage
import com.takamasafukase.ar_gunman_android.model.AndroidToUnityMessageEventType
import com.takamasafukase.ar_gunman_android.R
import com.takamasafukase.ar_gunman_android.UnityMessageCenter
import com.takamasafukase.ar_gunman_android.extensions.timeCountText
import com.takamasafukase.ar_gunman_android.features.game.weaponResources.soundResources
import com.takamasafukase.ar_gunman_android.features.game.weaponResources.uiResources
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class GameViewModel(
    gameStore: GameStoreInterface,
    weaponStore: WeaponStoreInterface,
    private val arShootingEngineHandler: ARShootingEngineHandlerInterface,
    private val motionSensorHandler: MotionSensorHandlerInterface,
    private val soundPlayer: SoundPlayerInterface,
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
        val sightImageId: Int = 0,
        val bulletsCountImageName: String = "",
        val isWeaponChangeButtonEnabled: Boolean = false,
        val isTutorialViewPresented: Boolean = false,
        val isWeaponSelectViewPresented: Boolean = false,
    )

    private val isTutorialViewPresentedFlow = MutableStateFlow(value = false)
    private val isWeaponSelectViewPresentedFlow = MutableStateFlow(value = false)
    private val _showResultEvent = MutableSharedFlow<Double>()

    val uiState: StateFlow<UIState> = combine(
        gameStore.gameFlow,
        gameStore.timeCount,
        weaponStore.weapon,
        isTutorialViewPresentedFlow,
        isWeaponSelectViewPresentedFlow,
    ) { gameFlow, timeCount, weapon, isTutorialViewPresented, isWeaponSelectViewPresented ->
        UIState(
            timeCountText = timeCount.countMillisec.timeCountText,
            currentWeaponType = weapon.currentType,
            sightImageId = weapon.currentType.uiResources.sightImageId,
            bulletsCountImageName = weapon.currentType.uiResources.bulletsCountImageName(
                bulletsCount = weapon.bulletsCount
            ),
            isWeaponChangeButtonEnabled = gameFlow.status.isTimerRunning,
            isTutorialViewPresented = isTutorialViewPresented,
            isWeaponSelectViewPresented = isWeaponSelectViewPresented,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
        initialValue = UIState(),
    )

    // 結果画面で表示する得点と一緒に線に指示を流す
    val showResultEvent get() = _showResultEvent.asSharedFlow()

    init {
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
                            isWeaponSelectViewPresentedFlow.value = false
                        }

                        GameFlowStatus.FlowEnded -> {
                            soundPlayer.play(SoundType.RANKING_APPEAR)
                            viewModelScope.launch {
                                // 結果画面への遷移指示を流す（結果画面で表示する得点も一緒に渡す）
                                _showResultEvent.emit(gameStore.score.value.value)
                            }
                        }

                        is GameFlowStatus.Blocked -> {
                            when (status.reason) {
                                GameFlowStatus.BlockedReason.TUTORIAL_NOT_COMPLETED -> {
                                    isTutorialViewPresentedFlow.value = true
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

//        viewModelScope.launch {
//            currentWeapon.fired
//                .collect {
//                    // 現在の武器の射撃命令のメッセージを作成
//                    val toUnityMessage = AndroidToUnityMessage(
//                        eventType = AndroidToUnityMessageEventType.FIRE_WEAPON,
//                        weaponType = currentWeapon.weaponTypeChanged.value,
//                    )
//                    UnityMessageCenter.sendMessageToUnity(toUnityMessage)
//                }
//        }
    }

    fun onViewAppear() {

    }

    fun onViewDisappear() {

    }

    fun onTapWeaponChangeButton() {
        // ゲーム開始前の場合は弾く
        if (!isGameStarted) return

        _state.value = _state.value.copy(isShowWeaponChangeDialog = true)
    }

    fun onCloseWeaponChangeDialog() {
        // ダイアログを閉じる
        _state.value = _state.value.copy(isShowWeaponChangeDialog = false)

        // TODO: 武器が変更されずにただcloseやエッジスワイプで閉じられた時も含めて鳴らしたい
    }

    fun onCloseTutorialDialog() {
        // ダイアログを閉じる
        _state.value = _state.value.copy(isShowTutorialDialog = false)

        viewModelScope.launch {
            // ゲーム画面で既にチュートリアルを見たというフラグを保存する
            tutorialPreferencesRepository.saveTutorialSeenStatus(true)
        }

        // チュートリアルを既に見ていた時の処理を行わせる
        handleTutorialSeenStatus(true)
    }

    fun onSelectWeapon(selectedWeapon: WeaponType) {
        // 今は一旦ピストル以外は弾く
        if (selectedWeapon != WeaponType.PISTOL) {
            return
        }

        // currentWeaponTypeを更新する
        currentWeapon.changeWeaponTypeTo(newType = selectedWeapon)

        // Unityへ武器表示の通知を送る
        // TODO: ここは武器が2つ以上に増えた時に実装する。今は武器の切り替えが無いので実装不要。

        // ダイアログを閉じる
        onCloseWeaponChangeDialog()
    }

    private fun checkTutorialSeenStatus() {
        viewModelScope.launch {
            tutorialPreferencesRepository.getTutorialSeenStatus(
                onData = { isAlreadySeen ->
                    handleTutorialSeenStatus(isAlreadySeen)
                }
            )
        }
    }

    private fun handleTutorialSeenStatus(isAlreadySeen: Boolean) {
        if (isAlreadySeen) {
            // すでにチュートリアルを見終わっている時の処理
            // デフォルトの武器を選択
            onSelectWeapon(selectedWeapon = WeaponType.PISTOL)

            // 1.5秒後にタイマーを開始
            Handler(Looper.getMainLooper()).postDelayed({
                // ゲーム開始フラグをtrueに変更
                isGameStarted = true

                // スタート音声を再生
                audioManager.playSound(R.raw.start_whistle)

                // タイマーを開始
                timeCounter.startTimer()
            }, 1500)

        } else {
            // まだチュートリアルを見ていない時の処理
            // チュートリアルダイアログの表示
            _state.value = _state.value.copy(isShowTutorialDialog = true)
        }
    }

    private fun handleMotionDetector(sensorManager: SensorManager) {
        // TODO: MotionDetectorのイベントもFlowでリアクティブにして、isGameStartedでフィルタリングできる様にしたい
        motionDetector = MotionDetector(
            sensorManager = sensorManager,
            onDetectWeaponFiringMotion = {
                // ゲーム開始後のみ処理をする
                if (isGameStarted) {
                    // 現在の武器に発射処理を行わせる
                    currentWeapon.fire()
                }
            },
            onDetectWeaponReloadingMotion = {
                // ゲーム開始後のみ処理をする
                if (isGameStarted) {
                    // 現在の武器にリロード処理を行わせる
                    currentWeapon.reload()
                }
            }
        )
    }

    private fun handleTargetHit() {
        // ターゲットヒット時の音声を再生
        audioManager.playSound(R.raw.target_hit)
        // 現在の武器に応じた得点の加算処理を行わせる
        scoreCounter.addScore(weaponType = currentWeapon.weaponTypeChanged.value)
    }
}

