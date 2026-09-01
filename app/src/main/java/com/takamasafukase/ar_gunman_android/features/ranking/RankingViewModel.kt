package com.takamasafukase.ar_gunman_android.features.ranking

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ar_gunman_android.domain.entities.ranking.RankingItem
import com.ar_gunman_android.domain.storeInterfaces.RankingStoreInterface
import com.ar_gunman_android.domain.useCases.RankingGetUseCaseInterface
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class RankingViewModel(
    private val rankingGetUseCase: RankingGetUseCaseInterface,
    rankingStore: RankingStoreInterface,
) : ViewModel() {
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
            Log.d("Android", "ログAndroid: RankingVM getRanking error: $error")
        }
    }
}