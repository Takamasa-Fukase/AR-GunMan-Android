package com.takamasafukase.ar_gunman_android.utility

import com.ar_gunman_android.domain.entities.ranking.Ranking

class RankingUtil {
    // 何位中/何位の表示テキストを作成
    fun createTemporaryRankText(
        rankingList: List<Ranking>,
        score: Double,
    ): String {
        // スコア表示は1から始まるので＋1する
        val temporaryRankNumber = getTemporaryRankIndex(rankingList, score) + 1
        return "$temporaryRankNumber / ${rankingList.size}"
    }

    // 取得したランキング順位の中から今回のスコア（まだ未登録）を差し込むと暫定何位になるかを計算して返却
    fun getTemporaryRankIndex(
        rankingList: List<Ranking>,
        score: Double,
    ): Int {
        // スコアの高い順になっているリストの中から最初にtotalScore以下のランクのindex番号を取得
        return rankingList.indexOfFirst {
            it.score <= score
        }
    }
}