package com.takamasafukase.ar_gunman_android.viewModel

import android.app.Application
import android.content.Intent
import android.os.Handler
import android.os.Looper
import androidx.compose.foundation.lazy.LazyListState
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.takamasafukase.ar_gunman_android.R
import com.takamasafukase.ar_gunman_android.entity.Ranking
import com.takamasafukase.ar_gunman_android.manager.AudioManager
import com.takamasafukase.ar_gunman_android.repository.RankingRepository
import com.takamasafukase.ar_gunman_android.utility.RankingUtil
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

data class ResultViewState(
    val rankings: List<Ranking>,
    val isShowNameRegisterDialog: Boolean,
    val isShowButtons: Boolean,
    val rankingListHighlightedIndex: Int?,
)

class ResultViewModel(
    app: Application,
    private val audioManager: AudioManager,
    private val rankingRepository: RankingRepository?,
    private val rankingUtil: RankingUtil,
) : AndroidViewModel(app) {
    private val _state = MutableStateFlow(
        ResultViewState(
            rankings = listOf(),
            isShowNameRegisterDialog = false,
            isShowButtons = false,
            rankingListHighlightedIndex = null,
        )
    )
    val state = _state.asStateFlow()
    private val rankingListFlow = MutableStateFlow<List<Ranking>>(value = listOf())
    val rankingListEvent = rankingListFlow.asStateFlow()
    val lazyListState = LazyListState()

    init {
        getRankings()

        viewModelScope.launch {
            _state
                .map { it.rankings }
                .collect {
                    rankingListFlow.value = it
                }
        }
    }

    fun onViewDidAppear() {
        // 結果画面と名前登録ダイアログの出現音声を再生
        audioManager.playSound(R.raw.ranking_appear)

        Handler(Looper.getMainLooper()).postDelayed({
            // 0.5秒後に名前登録ダイアログを表示させる指示を流す
            _state.value = _state.value.copy(isShowNameRegisterDialog = true)
        }, 500)
    }

    fun onCloseNameRegisterDialog(registeredRanking: Ranking?) {
        _state.value = _state.value.copy(isShowNameRegisterDialog = false)

        Handler(Looper.getMainLooper()).postDelayed({
            // 0.1秒後にボタンの出現アニメーションを開始させる
            _state.value = _state.value.copy(isShowButtons = true)
        }, 100)

        // 受け取ったランキングデータがnullじゃ無い場合（ユーザーが登録をした）の処理
        if (registeredRanking != null) {
            val newRankingList = _state.value.rankings.toMutableList()
            val rankIndex = rankingUtil.getTemporaryRankIndex(
                rankingList = newRankingList,
                score = registeredRanking.score,
            )

            //  そのデータを該当順位の位置に挿入してデータを流してリストを更新
            newRankingList.add(rankIndex, registeredRanking)
            _state.value = _state.value.copy(
                rankings = newRankingList,
                rankingListHighlightedIndex = rankIndex,
            )

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
        Handler(Looper.getMainLooper()).postDelayed({
            _state.value = _state.value.copy(
                isShowButtons = false,
                rankingListHighlightedIndex = null,
            )
            viewModelScope.launch {
                lazyListState.scrollToItem(
                    index = 0,
                )
            }
        }, 1000)
    }

    private fun getRankings() {
        rankingRepository?.getRankings(
            onData = {
                _state.value = _state.value.copy(rankings = it)
            },
            onError = {
                // Broadcastでエラーを通知して最上階層でアラートダイアログ表示させる
                val intent = Intent("ERROR_EVENT")
                intent.putExtra("errorMessage", it.message)
                LocalBroadcastManager
                    .getInstance(getApplication())
                    .sendBroadcast(intent)
            }
        )
    }
}