package com.ar_gunman_android.arshootingengine

import android.content.Context
import android.view.View
import androidx.activity.ComponentActivity
import com.ar_gunman_android.arshootingengine.mocks.ARShootingControllerMock

object ARShootingEngineFactory {
    fun getInstance(
        activity: ComponentActivity
    ): Pair<ARShootingControllerInterface, View> {
        ARShootingController.initialize(activity)
        return Pair(ARShootingController, ARShootingController.rootView)
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