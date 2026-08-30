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

class RankingViewModel(
    app: Application,
    private val rankingGetUseCase: RankingGetUseCaseInterface,
    rankingStore: RankingStoreInterface,
) : AndroidViewModel(app) {
    data class UIState(
        val dataList: List<RankingItem> = emptyList(),
    )

    val uiState: StateFlow<UIState> = rankingStore.ranking
        .map { ranking ->
            UIState(dataList = ranking?.items ?: emptyList())
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
            initialValue = UIState(),
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