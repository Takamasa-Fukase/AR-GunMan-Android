package com.takamasafukase.ar_gunman_android.features.ranking

import android.app.Application
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.ar_gunman_android.domain.entities.ranking.RankingItem
import com.ar_gunman_android.domain.storeInterfaces.RankingStoreInterface
import com.ar_gunman_android.domain.useCases.RankingGetUseCaseInterface
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class RankingViewState(
    val dataList: List<RankingItem>,
)

class RankingViewModel(
    app: Application,
    private val rankingGetUseCase: RankingGetUseCaseInterface,
    private val rankingStore: RankingStoreInterface,
) : AndroidViewModel(app) {
    val list = rankingStore.ranking
        .map { ranking ->
            RankingViewState(dataList = ranking?.items ?: emptyList())
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
            initialValue = RankingViewState(dataList = listOf()),
        )

    val uiState: StateFlow<RankingViewState> = rankingStore.ranking
        .map { ranking ->
            RankingViewState(dataList = ranking?.items ?: emptyList())
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
            initialValue = RankingViewState(dataList = listOf()),
        )

    init {
        getRanking()
    }

    private fun getRanking() {
        try {
            viewModelScope.launch {
                rankingGetUseCase.execute()
            }

        } catch (error: Exception) {
            // Broadcastでエラーを通知して最上階層でアラートダイアログ表示させる
            val intent = Intent("ERROR_EVENT")
            intent.putExtra("errorMessage", error.message)
            LocalBroadcastManager
                .getInstance(getApplication<Application>())
                .sendBroadcast(intent)
        }
    }
}