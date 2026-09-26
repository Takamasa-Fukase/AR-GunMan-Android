package com.ar_gunman_android.arshootingengine

import android.content.Context
import android.view.View
import androidx.activity.ComponentActivity
import com.ar_gunman_android.arshootingengine.mocks.ARShootingControllerMock

object ARShootingEngineFactory {
    fun create(
        activity: ComponentActivity
    ): Pair<ARShootingControllerInterface, View> {
        val controller = ARShootingController(
            activity = activity
        )
        return Pair(controller, controller.rootView)
    }

    fun createMock(
        context: Context
    ): Pair<ARShootingControllerInterface, View> {
        return Pair(
            ARShootingControllerMock(),
            View(context)
        )
    }
}