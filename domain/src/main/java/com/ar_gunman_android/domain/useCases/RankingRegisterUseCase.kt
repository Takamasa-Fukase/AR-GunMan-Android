package com.ar_gunman_android.domain.useCases

import com.ar_gunman_android.domain.entities.ranking.RankingItem
import com.ar_gunman_android.domain.repositoryInterfaces.RankingRepositoryInterface
import com.ar_gunman_android.domain.storeInterfaces.RankingStoreInterface

interface RankingRegisterUseCaseInterface {
    suspend fun execute(item: RankingItem)
}

class RankingRegisterUseCase(
    private val rankingRepository: RankingRepositoryInterface,
    private val rankingStore: RankingStoreInterface,
) : RankingRegisterUseCaseInterface {
    override suspend fun execute(item: RankingItem) {
        rankingRepository.registerItem(item = item)
        rankingStore.updateRanking { ranking ->
            ranking?.insertRegisteredRanking(item = item)
        }
    }
}