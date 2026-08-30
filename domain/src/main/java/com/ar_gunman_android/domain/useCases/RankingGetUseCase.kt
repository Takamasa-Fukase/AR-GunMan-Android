package com.ar_gunman_android.domain.useCases

import com.ar_gunman_android.domain.entities.ranking.Ranking
import com.ar_gunman_android.domain.repositoryInterfaces.RankingRepositoryInterface
import com.ar_gunman_android.domain.storeInterfaces.RankingStoreInterface

interface RankingGetUseCaseInterface {
    suspend fun execute()
}

class RankingGetUseCase(
    private val rankingRepository: RankingRepositoryInterface,
    private val rankingStore: RankingStoreInterface,
) : RankingGetUseCaseInterface {
    override suspend fun execute() {
        val items = rankingRepository.getItems()
        rankingStore.updateRanking {
            Ranking(items = items)
        }
    }
}