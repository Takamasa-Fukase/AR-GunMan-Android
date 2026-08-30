package com.takamasafukase.ar_gunman_android.features.game.weaponResources

import androidx.compose.ui.graphics.Color

data class WeaponUIResources(
    val weaponImageId: Int,
    val sightImageId: Int,
    val sightImageColor: Color,
    private val bulletsCountImageBaseName: String,
) {
    fun bulletsCountImageName(bulletsCount: Int): String {
        return bulletsCountImageBaseName + bulletsCount.toString()
    }
}
