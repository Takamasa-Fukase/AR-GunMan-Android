//package com.ar_gunman_android.domain.entities.weapon
//
//data class Pistol(
//    override val id: Int,
//    override val isDefault: Boolean,
//    override val spec: Spec,
//    override val resources: Resources
//) : Weapon<Pistol.Spec, Pistol.Resources> {
//
//    data class Spec(
//        override var capacity: Int,
//        override val reloadWaitingTimeMilliSec: Int,
//        override val reloadType: ReloadType,
//        override val targetHitPoint: Int
//    ) : WeaponSpec
//
//    data class Resources(
//        override val weaponImageName: String,
//        override val sightImageName: String,
//        override val sightImageColorType: ColorType,
//        override val bulletsCountImageBaseName: String,
//        override val appearingSound: SoundType,
//        override val firingSound: SoundType,
//        override val reloadingSound: SoundType
//    ) : WeaponResources
//}
