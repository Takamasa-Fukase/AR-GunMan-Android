package com.ar_gunman_android.arshootingengine.models

import kotlinx.serialization.Serializable

@Serializable
data class UnityToAndroidMessage(
    val eventType: UnityToAndroidMessageEventType,
)

@Serializable(with = UnityToAndroidMessageEventTypeSerializer::class)
enum class UnityToAndroidMessageEventType {
    TARGET_HIT,
}