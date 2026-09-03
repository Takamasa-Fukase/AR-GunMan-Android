package com.takamasafukase.ar_gunman_android.features.result

import android.util.Log
import androidx.compose.foundation.lazy.LazyListState
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ar_gunman_android.device.sound.SoundPlayerInterface
import com.ar_gunman_android.device.sound.SoundType
import com.ar_gunman_android.domain.entities.ranking.RankingItem
import com.ar_gunman_android.domain.storeInterfaces.RankingStoreInterface
import com.ar_gunman_android.domain.useCases.RankingGetUseCaseInterface
import com.takamasafukase.ar_gunman_android.constants.SavedStateHandleKeys
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ResultViewModel(
    savedStateHandle: SavedStateHandle,
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
    val score: Double = savedStateHandle.get<Double>("score") ?: 0.0

    init {
        getRanking()

        viewModelScope.launch {
            savedStateHandle
                .getStateFlow<RankingItem?>(SavedStateHandleKeys.REGISTERED_RANKING_ITEM, null)
                .drop(1)
                .collect { rankingItem ->
                    onCloseNameRegisterDialog(registeredRankingItem = rankingItem)
                }
        }
    }

    fun onViewAppear() {
        // 結果画面と名前登録ダイアログの出現音声を再生
        soundPlayer.play(SoundType.RANKING_APPEAR)

        viewModelScope.launch {
            // 0.5秒後に名前登録ダイアログを表示させる指示を流す
            delay(timeMillis = 500)
            _outputEvent.emit(OutputEventType.ShowNameRegisterView(score = score))
        }
    }

    // TODO: 暫定対応
    fun resetParams() {
        viewModelScope.launch {
            delay(timeMillis = 1000)
            isButtonsVisibleFlow.value = false
            rankingListHighlightedIndexFlow.value = null
            lazyListState.scrollToItem(
                index = 0,
            )
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

    private fun onCloseNameRegisterDialog(registeredRankingItem: RankingItem?) {
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

            //  該当データがリストの1番上にくる位置にスクロールさせる
            viewModelScope.launch {
                lazyListState.scrollToItem(
                    index = rankIndex,
                    scrollOffset = -24,
                )
            }
        }
    }
}