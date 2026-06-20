package com.takamasafukase.ar_gunman_android.const

import com.takamasafukase.ar_gunman_android.R
import com.takamasafukase.ar_gunman_android.model.TutorialPageContent

class TutorialConst {
    companion object {
        val pageContents: List<TutorialPageContent> = listOf(
            TutorialPageContent(
                titleText = "SHOOT",
                descriptionText = "Push device towards targets",
                imageResourceIds = listOf(
                    R.drawable.how_to_shoot_0,
                    R.drawable.how_to_shoot_1,
                ),
            ),
            TutorialPageContent(
                titleText = "RELOAD",
                descriptionText = "Rotate device",
                imageResourceIds = listOf(
                    R.drawable.how_to_reload_0,
                    R.drawable.how_to_reload_1,
                ),
            ),
            TutorialPageContent(
                titleText = "CHANGE WEAPON",
                descriptionText = "Tap this icon",
                imageResourceIds = listOf(
                    R.drawable.how_to_switch_weapon,
                ),
            ),
        )
    }
}