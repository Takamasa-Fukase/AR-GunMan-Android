package com.takamasafukase.ar_gunman_android.viewModel

import android.hardware.SensorManager
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.takamasafukase.ar_gunman_android.model.AndroidToUnityMessage
import com.takamasafukase.ar_gunman_android.model.AndroidToUnityMessageEventType
import com.takamasafukase.ar_gunman_android.model.WeaponType
import com.takamasafukase.ar_gunman_android.manager.AudioManager
import com.takamasafukase.ar_gunman_android.manager.MotionDetector
import com.takamasafukase.ar_gunman_android.R
import com.takamasafukase.ar_gunman_android.UnityMessageCenter
import com.takamasafukase.ar_gunman_android.manager.CurrentWeapon
import com.takamasafukase.ar_gunman_android.manager.ScoreCounter
import com.takamasafukase.ar_gunman_android.manager.TimeCounter
import com.takamasafukase.ar_gunman_android.repository.TutorialPreferencesRepository
import com.takamasafukase.ar_gunman_android.utility.TimeCountUtil
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

data class GameViewState(
    val isLoading: Boolean,
    val isShowTutorialDialog: Boolean,
    val isShowWeaponChangeDialog: Boolean,
    val timeCountText: String,
    val bulletsCountImageResourceId: Int,
)

class GameViewModel(
    sensorManager: SensorManager,
    private val tutorialPreferencesRepository: TutorialPreferencesRepository,
    private val audioManager: AudioManager,
    private val timeCounter: TimeCounter,
    private val timeCountUtil: TimeCountUtil,
    private val currentWeapon: CurrentWeapon,
    private val scoreCounter: ScoreCounter,
) : ViewModel() {

    private lateinit var motionDetector: MotionDetector
    private val _state = MutableStateFlow(
        GameViewState(
            isLoading = true,
            isShowTutorialDialog = false,
            isShowWeaponChangeDialog = false,
            timeCountText = "",
            bulletsCountImageResourceId = 0,
        )
    )
    val state = _state.asStateFlow()

    // 結果画面で表示する得点と一緒に線に指示を流す
    private val _showResult = MutableSharedFlow<Double>()
    val showResult = _showResult.asSharedFlow()

    private val onReceivedTargetHitEvent = MutableSharedFlow<Unit>()
    private var isGameStarted = false

    init {
        showLoadingToHideUnityLogoSplash()
        handleMotionDetector(sensorManager = sensorManager)

        viewModelScope.launch {
            UnityMessageCenter.targetHitEvent
                .debounce(50)
                .collect {
                    handleTargetHit()
                }
        }

        viewModelScope.launch {
            _state
                // isLoadingの値だけのflowに変換
                .map { MutableStateFlow(it.isLoading) }
                // falseの場合のみ通す
                .filter { isLoading -> !isLoading.value}
                // 最初の一回だけを通す
                .first()
                .collect {
                    // 初回のロードが終わった最初の1回だけを検知し、チュートリアル状態のチェックをする
                    checkTutorialSeenStatus()
                }
        }

        viewModelScope.launch {
            timeCounter.countChanged
                .collect {
                    _state.value = _state.value.copy(
                        timeCountText = timeCountUtil.getTwoDigitTimeCountText(it)
                    )
                }
        }

        viewModelScope.launch {
            timeCounter.countEnded
                .collect {
                    // 終了音声を再生
                    audioManager.playSound(R.raw.end_whistle)

                    // タイマーを破棄
                    timeCounter.disposeTimer()

                    // モーション検知を終了
                    motionDetector.stopUpdate()

                    // 1.5秒後に結果画面に遷移指示を流す
                    Handler(Looper.getMainLooper()).postDelayed({
                        viewModelScope.launch {
                            // 遷移指示を流す
                            _showResult.emit(
                                // 結果画面で表示する得点も一緒に渡す
                                scoreCounter.currentTotalScore.value
                            )
                        }
                    }, 1500)
                }
        }

        viewModelScope.launch {
            currentWeapon.weaponTypeChanged
                .collect {
                    // TODO: UnityにshowWeaponの通知を送る（これは武器が2つ以上に増えた時に実装する）
                }
        }

        viewModelScope.launch {
            currentWeapon.fired
                .collect {
                    // 現在の武器の射撃命令のメッセージを作成
                    val toUnityMessage = AndroidToUnityMessage(
                        eventType = AndroidToUnityMessageEventType.FIRE_WEAPON,
                        weaponType = currentWeapon.weaponTypeChanged.value,
                    )
                    UnityMessageCenter.sendMessageToUnity(toUnityMessage)
                }
        }

        viewModelScope.launch {
            currentWeapon.bulletsCountChanged
                .collect {
                    Log.d("Android", "ログAndroid: GameVM currentWeapon.bulletsCountChanged count: $it")
                    _state.value = _state.value.copy(
                        bulletsCountImageResourceId = currentWeapon.weaponTypeChanged.value
                            // 現在の残弾数に応じた画像を設定
                            .getBulletsCountImageResourceId(count = it)
                    )

                    // TODO: UnityにshowWeaponの通知を送る（これは武器が2つ以上に増えた時に実装する）
                }
        }
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

    // Unityビューを起動後にUnityロゴのスプラッシュが出るので、その間は黒背景とインジケータで隠す
    private fun showLoadingToHideUnityLogoSplash() {
        Handler(Looper.getMainLooper()).postDelayed({
            _state.value = _state.value.copy(isLoading = false)
        }, 3000)
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

        }else {
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

