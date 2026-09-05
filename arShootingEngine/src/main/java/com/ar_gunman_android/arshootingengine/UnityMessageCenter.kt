package com.ar_gunman_android.arshootingengine

import android.util.Log
import com.ar_gunman_android.arshootingengine.models.AndroidToUnityMessage
import com.ar_gunman_android.arshootingengine.models.UnityToAndroidMessage
import com.ar_gunman_android.arshootingengine.models.UnityToAndroidMessageEventType
import com.unity3d.player.UnityPlayer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

interface UnityMessageCenterInterface {
    val targetHitEvent: SharedFlow<Unit>
    @OptIn(InternalSerializationApi::class)
    fun sendMessageToUnity(message: AndroidToUnityMessage)
}

object UnityMessageCenter : UnityMessageCenterInterface {
    override val targetHitEvent: SharedFlow<Unit> get() = _targetHitEvent.asSharedFlow()
    private val _targetHitEvent = MutableSharedFlow<Unit>()
    private val scope = CoroutineScope(Dispatchers.Default)

    @OptIn(InternalSerializationApi::class)
    override fun sendMessageToUnity(message: AndroidToUnityMessage) {
        // JSON文字列に変換
        val jsonString = Json.encodeToString(message)
        // Unityへ通知を送る
        UnityPlayer.UnitySendMessage(
            "AndroidMessageCenterObject",
            "OnReceivedMessageFromAndroid",
            jsonString
        )
    }

    // Unity側から呼び出される
    fun onReceivedMessageFromUnity(message: String) {
        Log.d("Android", "ログAndroid: UnityMessageCenter onReceivedMessageFromUnity message: $message")

        val fromUnityMessage = Json.decodeFromString<UnityToAndroidMessage>(message)
        Log.d("Android", "ログAndroid: UnityMessageCenter fromUnityMessage: $fromUnityMessage, eventType: ${fromUnityMessage.eventType}")

        scope.launch {
            when (fromUnityMessage.eventType) {
                UnityToAndroidMessageEventType.TARGET_HIT -> {
                    _targetHitEvent.emit(Unit)
                }

                // TODO: debounce(50)の件を検討する
                //        viewModelScope.launch {
                //            UnityMessageCenter.targetHitEvent
                //                .debounce(50)
                //                .collect {
                //                    handleTargetHit()
                //                }
                //        }
            }
        }
    }
}