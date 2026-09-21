package com.takamasafukase.ar_gunman_android.features.result

import android.util.Log
import androidx.compose.foundation.lazy.LazyListState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ar_gunman_android.device.sound.SoundPlayerInterface
import com.ar_gunman_android.domain.entities.ranking.RankingItem
import com.ar_gunman_android.domain.storeInterfaces.RankingStoreInterface
import com.ar_gunman_android.domain.useCases.RankingGetUseCaseInterface
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ResultViewModel(
    val score: Double,
    private val soundPlayer: SoundPlayerInterface,
    private val rankingGetUseCase: RankingGetUseCaseInterface,
    private val rankingStore: RankingStoreInterface,
) : ViewModel() {
    data class UIState(
        val rankingItems: List<RankingItem> = emptyList(),
        val isButtonsVisible: Boolean = false,
        val rankingListHighlightedIndex: Int? = null,
    )
    sealed interface OutputEventType {
        data class ShowNameRegisterView(val score: Double) : OutputEventType
    }

    private val isButtonsVisibleFlow = MutableStateFlow(value = false)
    private val rankingListHighlightedIndexFlow = MutableStateFlow<Int?>(value = null)
    private val _outputEvent = MutableSharedFlow<OutputEventType>()

    val uiState: StateFlow<UIState> = combine(
        rankingStore.ranking,
        isButtonsVisibleFlow,
        rankingListHighlightedIndexFlow,
    ) { ranking, isButtonsVisible, rankingListHighlightedIndex ->
        UIState(
            rankingItems = ranking?.items ?: emptyList(),
            isButtonsVisible = isButtonsVisible,
            rankingListHighlightedIndex = rankingListHighlightedIndex,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
        initialValue = UIState(),
    )
    val outputEvent get() = _outputEvent.asSharedFlow()
    val lazyListState = LazyListState()

    fun onViewAppear() {
        getRanking()

        viewModelScope.launch {
            // 0.5秒後に名前登録ダイアログを表示させる指示を流す
            delay(timeMillis = 500)
            _outputEvent.emit(OutputEventType.ShowNameRegisterView(score = score))
        }
    }

    fun onCloseNameRegisterDialog(registeredRankingItem: RankingItem?) {
        viewModelScope.launch {
            // 0.1秒後にボタンの出現アニメーションを開始させる
            delay(timeMillis = 100)
            isButtonsVisibleFlow.value = true
        }

        // 受け取ったランキングデータがnullじゃ無い場合（ユーザーが登録をした）の処理
        if (registeredRankingItem != null) {
            val rankIndex = rankingStore.ranking.value?.getTentativeRankIndex(
                score = registeredRankingItem.score
            ) ?: 0

            // 該当データをハイライトさせる為のindexをセット
            rankingListHighlightedIndexFlow.value = rankIndex

            //  該当データがリストの1番上にくる位置にスクロールさせる
            viewModelScope.launch {
                lazyListState.scrollToItem(
                    index = rankIndex,
                    scrollOffset = -24,
                )
            }
        }
    }

    // MARK: - Private Methods
    private fun getRanking() {
        try {
            viewModelScope.launch {
                rankingGetUseCase.execute()
            }

        } catch (error: Exception) {
            Log.d("Android", "ログAndroid: ResultVM getRanking error: $error")
        }
    }
}