package com.ar_gunman_android.domain.repositoryInterfaces

import com.ar_gunman_android.domain.entities.ranking.Ranking

interface RankingRepositoryInterface {
    suspend fun getRanking(): List<Ranking>
    suspend fun registerRanking(ranking: Ranking)
}