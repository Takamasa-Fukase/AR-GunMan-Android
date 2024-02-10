package com.takamasafukase.ar_gunman_android.model

import com.takamasafukase.ar_gunman_android.R
import kotlinx.serialization.Serializable

@Serializable(with = WeaponTypeSerializer::class)
enum class WeaponType(
    val bulletsCapacity: Int,
    val hitPoint: Double,
    val setSoundResourceId: Int,
    val firingSoundResourceId: Int,
    val reloadSoundResourceId: Int,
    val weaponIconResourceId: Int,
) {
    PISTOL(
        bulletsCapacity = 7,
        hitPoint = 5.0,
        setSoundResourceId = R.raw.pistol_slide,
        firingSoundResourceId = R.raw.pistol_fire,
        reloadSoundResourceId = R.raw.pistol_reload,
        weaponIconResourceId = R.drawable.pistol,
    ),
    BAZOOKA(
        bulletsCapacity = 1,
        hitPoint = 12.0,
        setSoundResourceId = R.raw.bazooka_set,
        firingSoundResourceId = R.raw.bazooka_shoot,
        reloadSoundResourceId = R.raw.bazooka_reload,
        weaponIconResourceId = R.drawable.rocket_launcher,
    ),

    // TODO: 武器追加時に値を入れる
    RIFLE(
        bulletsCapacity = 0,
        hitPoint = 0.0,
        setSoundResourceId = 0,
        firingSoundResourceId = 0,
        reloadSoundResourceId = 0,
        weaponIconResourceId = R.drawable.rifle,
    ),
    SHOT_GUN(
        bulletsCapacity = 0,
        hitPoint = 0.0,
        setSoundResourceId = 0,
        firingSoundResourceId = 0,
        reloadSoundResourceId = 0,
        weaponIconResourceId = R.drawable.shot_gun,
    ),
    SNIPER_RIFLE(
        bulletsCapacity = 0,
        hitPoint = 0.0,
        setSoundResourceId = 0,
        firingSoundResourceId = 0,
        reloadSoundResourceId = 0,
        weaponIconResourceId = R.drawable.sniper_rifle,
    ),
    MINI_GUN(
        bulletsCapacity = 0,
        hitPoint = 0.0,
        setSoundResourceId = 0,
        firingSoundResourceId = 0,
        reloadSoundResourceId = 0,
        weaponIconResourceId = R.drawable.mini_gun,
    );

    fun getBulletsCountImageResourceId(count: Int): Int {
        return when (this) {
            PISTOL -> {
                when (count) {
                    0 -> R.drawable.bullets_0
                    1 -> R.drawable.bullets_1
                    2 -> R.drawable.bullets_2
                    3 -> R.drawable.bullets_3
                    4 -> R.drawable.bullets_4
                    5 -> R.drawable.bullets_5
                    6 -> R.drawable.bullets_6
                    7 -> R.drawable.bullets_7
                    else -> 0
                }
            }

            BAZOOKA -> {
                when (count) {
                    0 -> R.drawable.bazooka_rocket_0
                    1 -> R.drawable.bazooka_rocket_1
                    else -> 0
                }
            }

            else -> 0
        }
    }
}