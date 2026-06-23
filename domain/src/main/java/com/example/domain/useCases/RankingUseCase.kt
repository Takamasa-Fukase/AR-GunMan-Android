package com.example.domain.useCases

import com.example.domain.entities.ranking.Ranking
import com.example.domain.repositoryInterfaces.RankingRepositoryInterface

interface RankingUseCaseInterface {
    suspend fun getSortedRanking(): List<Ranking>
    suspend fun registerRanking(ranking: Ranking)
}

class RankingUseCase(
    private val rankingRepository: RankingRepositoryInterface
) : RankingUseCaseInterface {
    override suspend fun getSortedRanking(): List<Ranking> {
        return rankingRepository
            .getRanking()
            .sortedByDescending { it.score }
    }

    override suspend fun registerRanking(ranking: Ranking) {
        return rankingRepository.registerRanking(ranking)
    }
}