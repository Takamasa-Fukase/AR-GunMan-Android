package com.takamasafukase.ar_gunman_android.features.nameRegister

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ar_gunman_android.domain.entities.ranking.Ranking
import com.ar_gunman_android.domain.entities.ranking.RankingItem
import com.ar_gunman_android.domain.storeInterfaces.RankingStoreInterface
import com.ar_gunman_android.domain.useCases.RankingRegisterUseCaseInterface
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class NameRegisterViewModel(
    savedStateHandle: SavedStateHandle,
    private val rankingRegisterUseCase: RankingRegisterUseCaseInterface,
    rankingStore: RankingStoreInterface,
) : ViewModel() {
    data class UIState(
        val temporaryRankText: String? = null,
        val nameInputText: String = "",
        val isShowLoadingOnRegisterButton: Boolean = false,
    )

    private val nameInputTextFlow = MutableStateFlow(value = "")
    private val isShowLoadingOnRegisterButtonFlow = MutableStateFlow(value = false)
    private val _closeDialogEvent = MutableSharedFlow<RankingItem?>()

    private fun makeTemporaryRankText(ranking: Ranking?): String? {
        // ランキング取得中の場合はrankingがnilなのでnilを返す
        val _ranking = ranking ?: return null
        // 今回のscoreで仮に登録した場合の順位
        val temporaryRank = _ranking.getTentativeRankIndex(score = score) + 1
        // 登録済みランキング数に今回の結果を加えた数
        val totalCount = _ranking.items.size + 1
        return "$temporaryRank / $totalCount"
    }

    val uiState: StateFlow<UIState> = combine(
        rankingStore.ranking,
        nameInputTextFlow,
        isShowLoadingOnRegisterButtonFlow,
    ) { ranking, nameInputText, isShowLoadingOnRegisterButton ->
        UIState(
            temporaryRankText = makeTemporaryRankText(ranking = ranking),
            nameInputText = nameInputText,
            isShowLoadingOnRegisterButton = isShowLoadingOnRegisterButton,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
        initialValue = UIState(),
    )

    val closeDialogEvent get() = _closeDialogEvent.asSharedFlow()
    val score: Double = savedStateHandle.get<Double>("score") ?: 0.0

    fun onChangeNameText(text: String) {
        nameInputTextFlow.value = text
    }

    fun onTapNoThanksButton() {
        viewModelScope.launch {
            _closeDialogEvent.emit(null)
        }
    }

    fun onTapRegisterButton() {
        // 名前未入力の場合は弾く
        if (nameInputTextFlow.value.isEmpty()) { return }

        // ボタン上にインジケータ表示
        isShowLoadingOnRegisterButtonFlow.value = true

        // 入力された名前とスコアで新しいランキングを作成
        val newRankingItem = RankingItem(
            userName = nameInputTextFlow.value,
            score = score,
        )

        // 登録
        try {
            viewModelScope.launch {
                rankingRegisterUseCase.execute(item = newRankingItem)
                // 今回登録したランキングデータと一緒にダイアログを閉じる指示を流す
                _closeDialogEvent.emit(newRankingItem)
            }

        } catch (error: Exception) {
            Log.d("Android", "ログAndroid: RankingVM getRanking error: $error")
        }
    }
}