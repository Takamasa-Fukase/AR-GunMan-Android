package com.ar_gunman_android.arshootingengine

import com.ar_gunman_android.arshootingengine.models.WeaponType

interface ARShootingControllerInterface {
    val targetHit: ((WeaponType) -> Unit)?
    fun run()
    fun stop()
    fun showWeapon(type: WeaponType)
    fun renderWeaponFiring()
    fun changeTargetsAppearance()
}

internal class ARShootingController {

}