package com.example.domain.entities.weapon

enum class ColorType {
    RED,
    GREEN,
}

enum class ReloadType {
    MANUAL,
    AUTO,
}

interface WeaponSpec {
    var capacity: Int
    val reloadWaitingTimeMilliSec: Int
    val reloadType: ReloadType
    val targetHitPoint: Int
}

interface WeaponResources {
    val weaponImageName: String
    val sightImageName: String
    val sightImageColorType: ColorType
    val bulletsCountImageBaseName: String
    val appearingSound: SoundType
    val firingSound: SoundType
    val reloadingSound: SoundType
    val outOfBulletsSound: SoundType? get() = null
    val bulletHitSound: SoundType? get() = null
}

interface Weapon<Spec : WeaponSpec, Resources : WeaponResources> {
    val id: Int
    val isDefault: Boolean
    val spec: Spec
    val resources: Resources
}