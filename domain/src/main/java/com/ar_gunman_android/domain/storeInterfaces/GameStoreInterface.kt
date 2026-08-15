package com.ar_gunman_android.domain.storeInterfaces

import com.ar_gunman_android.domain.entities.game.GameFlow
import com.ar_gunman_android.domain.entities.game.GameScore
import com.ar_gunman_android.domain.entities.game.GameTimeCount
import com.ar_gunman_android.domain.entities.game.ReloadingMotionDetectedCount
import kotlinx.coroutines.flow.StateFlow

interface GameStoreInterface {
    val gameFlow: StateFlow<GameFlow>
    val timeCount: StateFlow<GameTimeCount>
    val score: StateFlow<GameScore>
    val reloadingMotionDetectedCount: StateFlow<ReloadingMotionDetectedCount>
    fun updateGameFlow(transform: (GameFlow) -> GameFlow)
    fun updateTimeCount(transform: (GameTimeCount) -> GameTimeCount)
    fun updateScore(transform: (GameScore) -> GameScore)
    fun <R> updateReloadingMotionDetectedCountWithResult(transform: (ReloadingMotionDetectedCount) -> Pair<ReloadingMotionDetectedCount, R>): R
    fun reset()
}