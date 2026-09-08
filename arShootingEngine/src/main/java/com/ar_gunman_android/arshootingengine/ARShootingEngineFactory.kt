package com.ar_gunman_android.arshootingengine

import android.view.View
import androidx.activity.ComponentActivity

object ARShootingEngineFactory {
    fun create(
        activity: ComponentActivity
    ): Pair<ARShootingControllerInterface, View> {
        val controller = ARShootingController(
            activity = activity
        )
        return Pair(controller, controller.rootView)
    }
}