package com.takamasafukase.ar_gunman_android.features.result

import android.app.Application
import android.content.Intent
import androidx.compose.foundation.lazy.LazyListState
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.ar_gunman_android.device.sound.SoundPlayerInterface
import com.ar_gunman_android.device.sound.SoundType
import com.ar_gunman_android.domain.entities.ranking.RankingItem
import com.ar_gunman_android.domain.storeInterfaces.RankingStoreInterface
import com.ar_gunman_android.domain.useCases.RankingGetUseCaseInterface
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ResultViewModel(
    app: Application,
    val score: Double,
    private val soundPlayer: SoundPlayerInterface,
    private val rankingGetUseCase: RankingGetUseCaseInterface,
    private val rankingStore: RankingStoreInterface,
) : AndroidViewModel(app) {
    data class UIState(
        val rankingItems: List<RankingItem> = emptyList(),
        val isShowNameRegisterDialog: Boolean = false,
        val isShowButtons: Boolean = false,
        val rankingListHighlightedIndex: Int? = null,
    )

    private val isShowNameRegisterDialogFlow = MutableStateFlow(value = false)
    private val isShowButtonsFlow = MutableStateFlow(value = false)
    private val rankingListHighlightedIndexFlow = MutableStateFlow<Int?>(value = null)

    val uiState: StateFlow<UIState> = combine(
        rankingStore.ranking,
        isShowNameRegisterDialogFlow,
        isShowButtonsFlow,
        rankingListHighlightedIndexFlow,
    ) { ranking, isShowNameRegisterDialog, isShowButtons, rankingListHighlightedIndex ->
        UIState(
            rankingItems = ranking?.items ?: emptyList(),
            isShowNameRegisterDialog = isShowNameRegisterDialog,
            isShowButtons = isShowButtons,
            rankingListHighlightedIndex = rankingListHighlightedIndex,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
        initialValue = UIState(),
    )
    val lazyListState = LazyListState()

    init {
        getRanking()
    }

    fun onViewDidAppear() {
        // 結果画面と名前登録ダイアログの出現音声を再生
        soundPlayer.play(SoundType.RANKING_APPEAR)

        viewModelScope.launch {
            // 0.5秒後に名前登録ダイアログを表示させる指示を流す
            delay(timeMillis = 500)
            isShowNameRegisterDialogFlow.value = true
        }
    }

    fun onCloseNameRegisterDialog(registeredRankingItem: RankingItem?) {
        isShowNameRegisterDialogFlow.value = false

        viewModelScope.launch {
            // 0.1秒後にボタンの出現アニメーションを開始させる
            delay(timeMillis = 100)
            isShowButtonsFlow.value = true
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

    // TODO: 暫定対応
    fun resetParams() {
        viewModelScope.launch {
            delay(timeMillis = 1000)
            isShowButtonsFlow.value = false
            rankingListHighlightedIndexFlow.value = null
            lazyListState.scrollToItem(
                index = 0,
            )
        }
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