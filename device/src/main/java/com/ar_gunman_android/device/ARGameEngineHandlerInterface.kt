package com.ar_gunman_android.device

interface ARShootingLibHandlerDelegate {
    fun targetHit()
}

interface ARGameEngineHandlerInterface {
    fun inject(delegate: ARShootingLibHandlerDelegate)
    fun runSession()
    fun pauseSession()
    fun showWeapon(id: Int)
    fun renderWeaponFiring()
    fun changeTargetAppearance(imageName: String)
}