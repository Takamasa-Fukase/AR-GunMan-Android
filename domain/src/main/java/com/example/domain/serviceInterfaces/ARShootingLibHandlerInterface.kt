package com.example.domain.serviceInterfaces

interface ARShootingLibHandlerDelegate {
    fun targetHit()
}

interface ARShootingLibHandlerInterface {
    fun inject(delegate: ARShootingLibHandlerDelegate)
    fun runSession()
    fun pauseSession()
    fun showWeapon(id: Int)
    fun renderWeaponFiring()
    fun changeTargetAppearance(imageName: String)
}