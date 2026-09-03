package com.takamasafukase.ar_gunman_android.model

import kotlinx.serialization.Serializable
import com.ar_gunman_android.domain.entities.weapon.WeaponType
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