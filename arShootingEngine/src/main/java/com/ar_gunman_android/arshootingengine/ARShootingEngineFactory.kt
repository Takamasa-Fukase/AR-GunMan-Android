package com.ar_gunman_android.arshootingengine

import android.content.Context
import android.view.View
import androidx.activity.ComponentActivity
import com.ar_gunman_android.arshootingengine.mocks.ARShootingControllerMock

object ARShootingEngineFactory {
    fun create(
        activity: ComponentActivity
    ): Triple<ARShootingControllerInterface, View, (() -> Unit)?> {
        val controller = ARShootingController(
            activity = activity
        )
        return Triple(controller, controller.rootView, controller.splashFinished)
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