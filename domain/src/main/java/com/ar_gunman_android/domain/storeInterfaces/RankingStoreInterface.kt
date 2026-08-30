package com.ar_gunman_android.domain.storeInterfaces

import com.ar_gunman_android.domain.entities.ranking.Ranking
import kotlinx.coroutines.flow.StateFlow

interface RankingStoreInterface {
    val ranking: StateFlow<Ranking?>
    fun updateRanking(transform: (Ranking?) -> Ranking?)
    fun reset()
}