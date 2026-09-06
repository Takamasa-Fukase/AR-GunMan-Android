package com.ar_gunman_android.arshootingengine

import androidx.activity.ComponentActivity

object ARShootingEngineFactory {
    fun create(
        activity: ComponentActivity
    ): ARShootingControllerInterface {
        val controller = ARShootingController(
            activity = activity
        )
        return controller
    }
}