package com.takamasafukase.ar_gunman_android

import android.util.Log
import com.takamasafukase.ar_gunman_android.model.AndroidToUnityMessage
import com.takamasafukase.ar_gunman_android.model.UnityToAndroidMessage
import com.takamasafukase.ar_gunman_android.model.UnityToAndroidMessageEventType
import com.unity3d.player.UnityPlayer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

interface UnityMessageCenterInterface {
    val targetHitEvent: SharedFlow<Unit>
    fun sendMessageToUnity(message: AndroidToUnityMessage)
}

object UnityMessageCenter : UnityMessageCenterInterface {
    override val targetHitEvent: SharedFlow<Unit> get() = receivedMessageFlow
        .filter { it.eventType == UnityToAndroidMessageEventType.TARGET_HIT }
        .map {}
        .shareIn(
            scope = CoroutineScope(Dispatchers.Default),
            started = SharingStarted.WhileSubscribed(5000),
            replay = 0
        )
    private val receivedMessageFlow = MutableSharedFlow<UnityToAndroidMessage>()

    override fun sendMessageToUnity(message: AndroidToUnityMessage) {
        // JSON文字列に変換
        val jsonString = Json.encodeToString(message)
        // Unityへ通知を送る
        UnityPlayer.UnitySendMessage("XR Origin", "OnReceiveMessageFromAndroid", jsonString)
    }

    // Unity側から呼び出される
    fun onReceivedMessageFromUnity(message: String) {
        Log.d("Android", "ログAndroid: UnityMessageCenter onReceivedMessageFromUnity message: $message")

        val fromUnityMessage = Json.decodeFromString<UnityToAndroidMessage>(message)
        Log.d("Android", "ログAndroid: UnityMessageCenter fromUnityMessage: $fromUnityMessage, eventType: ${fromUnityMessage.eventType}")

        CoroutineScope(Dispatchers.Default).launch {
            receivedMessageFlow.emit(fromUnityMessage)
        }
    }
}