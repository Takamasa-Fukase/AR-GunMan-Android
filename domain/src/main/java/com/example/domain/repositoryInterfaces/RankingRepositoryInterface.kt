package com.example.domain.repositoryInterfaces

import com.example.domain.entities.ranking.Ranking

interface RankingRepositoryInterface {
    suspend fun getRanking(): List<Ranking>
    suspend fun registerRanking(ranking: Ranking)
}