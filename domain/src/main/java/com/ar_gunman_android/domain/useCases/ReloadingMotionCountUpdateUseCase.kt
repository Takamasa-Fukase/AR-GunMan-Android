package com.ar_gunman_android.domain.useCases

import com.ar_gunman_android.domain.entities.game.ReloadingMotionDetectedCountUpdateResult
import com.ar_gunman_android.domain.storeInterfaces.GameStoreInterface

interface ReloadingMotionCountUpdateUseCaseInterface {
    fun execute(): ReloadingMotionDetectedCountUpdateResult
}

class ReloadingMotionCountUpdateUseCase(
    private var gameStore: GameStoreInterface
) : ReloadingMotionCountUpdateUseCaseInterface {
    override fun execute(): ReloadingMotionDetectedCountUpdateResult {
        return gameStore.updateReloadingMotionDetectedCountWithResult { count ->
            count.update()
        }
    }
}