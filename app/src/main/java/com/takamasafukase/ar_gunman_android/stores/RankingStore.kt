package com.takamasafukase.ar_gunman_android.stores

import com.ar_gunman_android.domain.entities.ranking.Ranking
import com.ar_gunman_android.domain.storeInterfaces.RankingStoreInterface
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

object RankingStore : RankingStoreInterface {
    override val ranking: StateFlow<Ranking?> get() = _ranking.asStateFlow()
    private val _ranking = MutableStateFlow<Ranking?>(value = null)

    override fun updateRanking(transform: (Ranking?) -> Ranking?) {
        _ranking.update(transform)
    }

    override fun reset() {
        _ranking.value = null
    }
}