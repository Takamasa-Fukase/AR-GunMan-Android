package com.ar_gunman_android.arshootingengine.models

import kotlinx.serialization.Serializable
import kotlinx.serialization.InternalSerializationApi

@InternalSerializationApi @Serializable
data class AndroidToUnityMessage(
    val eventType: AndroidToUnityMessageEventType,
    val weaponType: WeaponType,
)

@Serializable(with = AndroidToUnityMessageEventTypeSerializer::class)
enum class AndroidToUnityMessageEventType {
    SHOW_WEAPON,
    FIRE_WEAPON,
}