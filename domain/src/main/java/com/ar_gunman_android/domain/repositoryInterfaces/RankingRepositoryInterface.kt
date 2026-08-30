package com.ar_gunman_android.domain.repositoryInterfaces

import com.ar_gunman_android.domain.entities.ranking.RankingItem

interface RankingRepositoryInterface {
    suspend fun getItems(): List<RankingItem>
    suspend fun registerItem(item: RankingItem)
}