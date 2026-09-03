package com.ar_gunman_android.domain.entities.weapon

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.Serializer
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializer(forClass = WeaponType::class)
object WeaponTypeSerializer : KSerializer<WeaponType> {
    override fun serialize(encoder: Encoder, value: WeaponType) {
        encoder.encodeInt(value.ordinal)
    }

    override fun deserialize(decoder: Decoder): WeaponType {
        return WeaponType.values()[decoder.decodeInt()]
    }
}

@Serializable(with = WeaponTypeSerializer::class)
enum class WeaponType {
    PISTOL,
    BAZOOKA;

    enum class ReloadType {
        MANUAL,
        AUTO,
    }

    private data class WeaponInfo(
        val isDefault: Boolean,
        val capacity: Int,
        val reloadWaitingTimeMillisec: Int,
        val reloadType: ReloadType,
        val targetHitPoint: Int,
    )

    companion object {
        val defaultType: WeaponType
            get() = WeaponType.values().firstOrNull { it.isDefault }
                ?: error("デフォルトのWeaponTypeが存在しません")
    }

    val isDefault: Boolean
        get() = info.isDefault

    val capacity: Int
        get() = info.capacity

    val reloadWaitingTimeMillisec: Int
        get() = info.reloadWaitingTimeMillisec

    val reloadType: ReloadType
        get() = info.reloadType

    val targetHitPoint: Int
        get() = info.targetHitPoint

    private val info: WeaponInfo
        get() = when (this) {
            PISTOL -> WeaponInfo(
                isDefault = true,
                capacity = 7,
                reloadWaitingTimeMillisec = 0,
                reloadType = ReloadType.MANUAL,
                targetHitPoint = 5,
            )
            BAZOOKA -> WeaponInfo(
                isDefault = false,
                capacity = 1,
                reloadWaitingTimeMillisec = 3200,
                reloadType = ReloadType.AUTO,
                targetHitPoint = 12,
            )
        }
}

