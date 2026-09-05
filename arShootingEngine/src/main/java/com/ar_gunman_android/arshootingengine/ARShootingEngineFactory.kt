package com.ar_gunman_android.arshootingengine

object ARShootingEngineFactory {
    fun create(): ARShootingControllerInterface {
        val controller = ARShootingController()
        return controller
    }
}