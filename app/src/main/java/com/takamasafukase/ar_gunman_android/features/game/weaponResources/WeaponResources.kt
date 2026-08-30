package com.takamasafukase.ar_gunman_android.features.game.weaponResources

import androidx.compose.ui.graphics.Color
import com.ar_gunman_android.device.sound.SoundType
import com.takamasafukase.ar_gunman_android.R
import com.ar_gunman_android.domain.entities.weapon.WeaponType

val WeaponType.uiResources: WeaponUIResources
    get() = when (this) {
        WeaponType.PISTOL -> {
            WeaponUIResources(
                weaponImageId = R.drawable.pistol,
                sightImageId = R.drawable.pistol_sight,
                sightImageColor = Color.Red,
                bulletsCountImageBaseName = "pistol_bullets_"
            )
        }
        WeaponType.BAZOOKA -> {
            WeaponUIResources(
                weaponImageId = R.drawable.bazooka,
                sightImageId = R.drawable.bazooka_sight,
                sightImageColor = Color.Green,
                bulletsCountImageBaseName = "bazooka_bullets_"
            )
        }
    }

val WeaponType.soundResources: WeaponSoundResources
    get() = when (this) {
        WeaponType.PISTOL -> {
            PistolSoundResources(
                appearingSound = SoundType.PISTOL_APPEAR,
                firingSound = SoundType.PISTOL_FIRE,
                reloadingSound = SoundType.PISTOL_RELOAD,
                outOfBulletsSound = SoundType.PISTOL_OUT_OF_BULLETS,
            )
        }
        WeaponType.BAZOOKA -> {
            BazookaSoundResources(
                appearingSound = SoundType.BAZOOKA_APPEAR,
                firingSound = SoundType.BAZOOKA_FIRE,
                reloadingSound = SoundType.BAZOOKA_RELOAD,
                bulletHitSound = SoundType.BAZOOKA_EXPLOSION,
            )
        }
    }