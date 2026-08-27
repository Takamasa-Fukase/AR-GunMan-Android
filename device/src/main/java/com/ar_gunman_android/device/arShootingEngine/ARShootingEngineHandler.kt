package com.ar_gunman_android.device.arShootingEngine

import com.ar_gunman_android.domain.entities.weapon.WeaponType as DomainWeaponType

interface ARShootingEngineHandlerInterface {
    var targetHit: ((DomainWeaponType) -> Unit)?
    fun run()
    fun pause()
    fun showWeapon(type: DomainWeaponType)
    fun renderWeaponFiring()
    fun changeTargetsAppearance()
}

class ARShootingEngineHandler(
    override var targetHit: ((DomainWeaponType) -> Unit)?
//    private val arShootingController:
) : ARShootingEngineHandlerInterface {
    override fun run() {

    }

    override fun pause() {

    }

    override fun showWeapon(type: DomainWeaponType) {

    }

    override fun renderWeaponFiring() {

    }

    override fun changeTargetsAppearance() {

    }
}