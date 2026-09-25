package com.ar_gunman_android.arshootingengine.mocks

import com.ar_gunman_android.arshootingengine.ARShootingControllerInterface
import com.ar_gunman_android.arshootingengine.models.WeaponType

class ARShootingControllerMock() : ARShootingControllerInterface {
    override var splashFinished: (() -> Unit)? = null
    override var targetHit: ((WeaponType) -> Unit)? = null
    override fun run() {}
    override fun stop() {}
    override fun showWeapon(type: WeaponType) {}
    override fun renderWeaponFiring() {}
    override fun changeTargetsAppearance() {}
}