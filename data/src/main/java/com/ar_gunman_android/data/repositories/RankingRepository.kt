package com.ar_gunman_android.data.repositories

import com.ar_gunman_android.data.dataSources.FirestoreClientInterface
import com.ar_gunman_android.data.dataSources.FirestoreConst
import com.ar_gunman_android.data.models.RankingItemDto
import com.ar_gunman_android.data.models.toDto
import com.ar_gunman_android.domain.entities.ranking.RankingItem
import com.ar_gunman_android.domain.repositoryInterfaces.RankingRepositoryInterface

class RankingRepository(
    private val firestoreClient: FirestoreClientInterface
) : RankingRepositoryInterface {
    override suspend fun getItems(): List<RankingItem> {
        return firestoreClient
            .getItems(
                collectionPath = FirestoreConst.WORLD_RANKING,
                responseType = RankingItemDto::class.java
            )
            .map { it.toDomain() }
    }

    override suspend fun registerItem(item: RankingItem) {
        firestoreClient
            .addItem(
                collectionPath = FirestoreConst.WORLD_RANKING,
                request = item.toDto()
            )
    }
}