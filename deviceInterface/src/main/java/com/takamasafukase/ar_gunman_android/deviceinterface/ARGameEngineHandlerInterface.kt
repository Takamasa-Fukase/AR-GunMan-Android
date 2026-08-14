package com.takamasafukase.ar_gunman_android.deviceinterface

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