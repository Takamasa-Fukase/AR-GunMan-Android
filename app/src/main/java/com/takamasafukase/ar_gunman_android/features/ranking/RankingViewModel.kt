package com.takamasafukase.ar_gunman_android.features.ranking

import android.app.Application
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.ar_gunman_android.domain.entities.ranking.Ranking
import com.takamasafukase.ar_gunman_android.repositoryMock.RankingRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

data class RankingViewState(
    val rankings: List<Ranking>,
)

class RankingViewModel(app: Application) : AndroidViewModel(app) {
    private val _state = MutableStateFlow(RankingViewState(listOf()))
    val state = _state.asStateFlow()
    private val rankingRepository = RankingRepository()

    init {
        getRankings()
    }

    private fun getRankings() {
        rankingRepository.getRankings(
            onData = {
                _state.value = _state.value.copy(rankings = it)
            },
            onError = {
                // Broadcastでエラーを通知して最上階層でアラートダイアログ表示させる
                val intent = Intent("ERROR_EVENT")
                intent.putExtra("errorMessage", it.message)
                LocalBroadcastManager
                    .getInstance(getApplication<Application>())
                    .sendBroadcast(intent)
            }
        )
    }
}