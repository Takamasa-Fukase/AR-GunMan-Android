package com.takamasafukase.ar_gunman_android.viewModel

import android.hardware.SensorManager
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.takamasafukase.ar_gunman_android.Model.AndroidToUnityMessage
import com.takamasafukase.ar_gunman_android.Model.AndroidToUnityMessageEventType
import com.takamasafukase.ar_gunman_android.Model.UnityToAndroidMessage
import com.takamasafukase.ar_gunman_android.Model.UnityToAndroidMessageEventType
import com.takamasafukase.ar_gunman_android.Model.WeaponType
import com.takamasafukase.ar_gunman_android.manager.AudioManager
import com.takamasafukase.ar_gunman_android.manager.MotionDetector
import com.takamasafukase.ar_gunman_android.R
import com.takamasafukase.ar_gunman_android.UnityToAndroidMessenger
import com.unity3d.player.UnityPlayer
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.lang.ref.WeakReference

data class GameViewState(
    val isLoading: Boolean,
    val isShowWeaponChangeDialog: Boolean,
)

class GameViewModel(
    sensorManager: SensorManager,
    private val audioManager: AudioManager,
) : ViewModel(), UnityToAndroidMessenger.MessageReceiverFromUnity {
    private lateinit var motionDetector: MotionDetector
    private val _state = MutableStateFlow(
        GameViewState(
            isLoading = true,
            isShowWeaponChangeDialog = false,
        )
    )
    val state = _state.asStateFlow()
    private val onReceivedTargetHitEvent = MutableSharedFlow<Unit>()
    private var currentWeaponType = WeaponType.PISTOL;

    init {
        showLoadingToHideUnityLogoSplash()
        handleMotionDetector(sensorManager = sensorManager)
        // Unityからのメッセージの受け口になるオブジェクトの受け手として自身を弱参照で登録
        UnityToAndroidMessenger.receiver = WeakReference(this)

        viewModelScope.launch {
            onReceivedTargetHitEvent
                .debounce(50)
                .collect { _ ->
                    handleTargetHit()
                }
        }
    }

    fun onTapWeaponChangeButton() {
        _state.value = _state.value.copy(isShowWeaponChangeDialog = true)
    }

    fun onCloseWeaponChangeDialog() {
        _state.value = _state.value.copy(isShowWeaponChangeDialog = false)

        // TODO: あとでSwift版みたいにenumに紐づけてどこかのファイルに置いて、メソッドで取得できる様にしたい
        // TODO: 武器が変更されずにただcloseやエッジスワイプで閉じられた時も現在の武器の表示音声を鳴らす
        val weaponSetSoundResourceId = when (currentWeaponType) {
            WeaponType.PISTOL -> R.raw.pistol_slide
            else -> 0
        }
        // 選択された武器に対応した音声を再生
        audioManager.playSound(weaponSetSoundResourceId)
    }

    fun onSelectWeapon(selectedWeapon: WeaponType) {
        // 今は一旦ピストル以外は弾く
        if (selectedWeapon != WeaponType.PISTOL) {
            return
        }

        // currentWeaponTypeを更新する
        currentWeaponType = selectedWeapon

        // Unityへ武器表示の通知を送る
        // TODO: ここは武器が2つ以上に増えた時に実装する。今は武器の切り替えが無いので実装不要。

        // ダイアログを閉じる
        onCloseWeaponChangeDialog()
    }

    override fun onMessageReceivedFromUnity(message: String) {
        Log.d("Android", "ログAndroid: GameVM onMessageReceivedFromUnity message: $message")

        val fromUnityMessage = Json.decodeFromString<UnityToAndroidMessage>(message)
        Log.d("Android", "ログAndroid: GameVM fromUnityMessage: $fromUnityMessage, eventType: ${fromUnityMessage.eventType}")

        when (fromUnityMessage.eventType) {
            UnityToAndroidMessageEventType.TARGET_HIT -> {
                viewModelScope.launch {
                    onReceivedTargetHitEvent.emit(Unit)
                }
            }
        }
    }

    // Unityビューを起動後にUnityロゴのスプラッシュが出るので、その間は黒背景とインジケータで隠す
    private fun showLoadingToHideUnityLogoSplash() {
        Handler(Looper.getMainLooper()).postDelayed({
            _state.value = _state.value.copy(isLoading = false)
        }, 3000)
    }

    private fun handleMotionDetector(sensorManager: SensorManager) {
        motionDetector = MotionDetector(
            sensorManager = sensorManager,
            onDetectPistolFiringMotion = {
                Log.d("Android", "ログAndroid: onDetectPistolFiringMotion")
                audioManager.playSound(R.raw.pistol_fire)

                // ピストル射撃命令のメッセージを作成
                val toUnityMessage = AndroidToUnityMessage(
                    eventType = AndroidToUnityMessageEventType.FIRE_WEAPON,
                    weaponType = currentWeaponType,
                )
                // JSON文字列に変換
                val jsonString = Json.encodeToString(toUnityMessage)
                // Unityへ通知を送る
                UnityPlayer.UnitySendMessage("XR Origin", "OnReceiveMessageFromAndroid", jsonString)
            },
            onDetectPistolReloadingMotion = {
                Log.d("Android", "ログAndroid: onDetectPistolReloadingMotion")
                audioManager.playSound(R.raw.pistol_reload)
                // TODO: リロード後の処理
            }
        )
    }

    private fun handleTargetHit() {
        // ターゲットヒット時の音声を再生
        audioManager.playSound(R.raw.head_shot)
        // TODO: スコアの加算処理
    }
}

