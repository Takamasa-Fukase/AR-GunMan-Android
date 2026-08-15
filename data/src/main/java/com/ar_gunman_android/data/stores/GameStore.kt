package com.ar_gunman_android.data.stores

import com.ar_gunman_android.domain.entities.game.GameFlow
import com.ar_gunman_android.domain.entities.game.GameScore
import com.ar_gunman_android.domain.entities.game.GameTimeCount
import com.ar_gunman_android.domain.entities.game.ReloadingMotionDetectedCount
import com.ar_gunman_android.domain.storeInterfaces.GameStoreInterface
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

object GameStore : GameStoreInterface {
    override val gameFlow: StateFlow<GameFlow> get() = _gameFlow.asStateFlow()
    override val timeCount: StateFlow<GameTimeCount> get() = _timeCount.asStateFlow()
    override val score: StateFlow<GameScore> get() = _score.asStateFlow()
    override val reloadingMotionDetectedCount: StateFlow<ReloadingMotionDetectedCount> get() = _reloadingMotionDetectedCount.asStateFlow()

    private val _gameFlow = MutableStateFlow(value = GameFlow())
    private val _timeCount = MutableStateFlow(value = GameTimeCount())
    private val _score = MutableStateFlow(value = GameScore())
    private val _reloadingMotionDetectedCount = MutableStateFlow(value = ReloadingMotionDetectedCount())

    override fun updateGameFlow(transform: (GameFlow) -> GameFlow) {
        _gameFlow.update(transform)
    }

    override fun updateTimeCount(transform: (GameTimeCount) -> GameTimeCount) {
        _timeCount.update(transform)
    }

    override fun updateScore(transform: (GameScore) -> GameScore) {
        _score.update(transform)
    }

    override fun <R> updateReloadingMotionDetectedCountWithResult(transform: (ReloadingMotionDetectedCount) -> Pair<ReloadingMotionDetectedCount, R>): R {
        val (updatedCount, result) = transform(_reloadingMotionDetectedCount.value)
        _reloadingMotionDetectedCount.value = updatedCount
        return result
    }

    override fun reset() {
        _gameFlow.value = GameFlow()
        _timeCount.value = GameTimeCount()
        _score.value = GameScore()
        _reloadingMotionDetectedCount.value = ReloadingMotionDetectedCount()
    }
}