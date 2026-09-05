package com.ar_gunman_android.device.arShootingEngine

import com.ar_gunman_android.arshootingengine.ARShootingControllerInterface
import com.ar_gunman_android.arshootingengine.models.WeaponType as ARShootingWeaponType
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
    private val arShootingController: ARShootingControllerInterface
) : ARShootingEngineHandlerInterface {
    override var targetHit: ((DomainWeaponType) -> Unit)? = null

    init {
        arShootingController.targetHit = { weaponType ->
            targetHit?.invoke(weaponType.toDomainWeaponType)
        }
    }

    override fun run() {
        arShootingController.run()
    }

    override fun pause() {
        arShootingController.stop()
    }

    override fun showWeapon(type: DomainWeaponType) {
        arShootingController.showWeapon(type = type.toARShootingWeaponType)
    }

    override fun renderWeaponFiring() {
        arShootingController.renderWeaponFiring()
    }

    override fun changeTargetsAppearance() {
        arShootingController.changeTargetsAppearance()
    }
}

private val DomainWeaponType.toARShootingWeaponType: ARShootingWeaponType
    get() = when (this) {
        DomainWeaponType.PISTOL -> {
            ARShootingWeaponType.PISTOL
        }

        DomainWeaponType.BAZOOKA -> {
            ARShootingWeaponType.BAZOOKA
        }
    }

private val ARShootingWeaponType.toDomainWeaponType: DomainWeaponType
    get() = when (this) {
        ARShootingWeaponType.PISTOL -> {
            DomainWeaponType.PISTOL
        }

        ARShootingWeaponType.BAZOOKA -> {
            DomainWeaponType.BAZOOKA
        }
    }