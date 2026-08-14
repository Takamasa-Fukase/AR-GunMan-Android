package com.ar_gunman_android.domain.storeInterfaces

import com.ar_gunman_android.domain.entities.game.GameFlow
import com.ar_gunman_android.domain.entities.game.GameScore
import com.ar_gunman_android.domain.entities.game.GameTimeCount
import com.ar_gunman_android.domain.entities.game.ReloadingMotionDetectedCount

interface GameStoreInterface {
    var gameFlow: GameFlow
    var timeCount: GameTimeCount
    var score: GameScore
    var reloadingMotionDetectedCount: ReloadingMotionDetectedCount
    fun reset()
}