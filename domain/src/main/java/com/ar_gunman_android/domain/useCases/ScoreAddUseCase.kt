package com.ar_gunman_android.domain.useCases

import com.ar_gunman_android.domain.storeInterfaces.GameStoreInterface

interface ScoreAddUseCaseInterface {
    fun execute(targetHitPoint: Int)
}

class ScoreAddUseCase(
    private val gameStore: GameStoreInterface
) : ScoreAddUseCaseInterface {
    override fun execute(targetHitPoint: Int) {
        gameStore.updateScore { score ->
            score.add(targetHitPoint = targetHitPoint)
        }
    }
}