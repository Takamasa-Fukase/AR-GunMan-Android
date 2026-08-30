package com.ar_gunman_android.data.repositories.stubs

import com.ar_gunman_android.domain.entities.ranking.RankingItem
import com.ar_gunman_android.domain.repositoryInterfaces.RankingRepositoryInterface
import kotlinx.coroutines.delay

object RankingRepositoryStub : RankingRepositoryInterface {
    private lateinit var items: MutableList<RankingItem>

    init {
        reset()
    }

    override suspend fun getItems(): List<RankingItem> {
        delay(timeMillis = 1500)
        return items.toList()
    }

    override suspend fun registerItem(item: RankingItem) {
        delay(timeMillis = 1500)
        items.add(item)
    }

    fun reset() {
        val listSize = 100
        items = (1..listSize).map { num ->
            RankingItem(
                score = ((listSize + 1) - num).toDouble(),
                userName = "ユーザー$num"
            )
        }.toMutableList()
    }
}
