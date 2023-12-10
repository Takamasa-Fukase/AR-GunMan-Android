package com.takamasafukase.ar_gunman_android.viewModel

import android.hardware.SensorManager
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.ViewModel
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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.lang.ref.WeakReference

data class GameViewState(
    val isLoading: Boolean,
)

class GameViewModel(
    sensorManager: SensorManager,
    private val audioManager: AudioManager,
) : ViewModel(), UnityToAndroidMessenger.MessageReceiverFromUnity {
    private lateinit var motionDetector: MotionDetector
    private val _state = MutableStateFlow(GameViewState(true))
    val state = _state.asStateFlow()

    init {
        showLoadingToHideUnityLogoSplash()
        handleMotionDetector(sensorManager = sensorManager)
        // Unityからのメッセージの受け口になるオブジェクトの受け手として自身を弱参照で登録
        UnityToAndroidMessenger.receiver = WeakReference(this)
    }

    fun onTapWeaponChangeButton() {

    }

    override fun onMessageReceivedFromUnity(message: String) {
        Log.d("Android", "ログAndroid: GameVM onMessageReceivedFromUnity message: $message")

        val fromUnityMessage = Json.decodeFromString<UnityToAndroidMessage>(message)
        when (fromUnityMessage.eventType) {
            UnityToAndroidMessageEventType.TARGET_HIT -> {
                // TODO: スコアの加算処理
                audioManager.playSound(R.raw.head_shot)
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
                    weaponType = WeaponType.PISTOL,
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
}