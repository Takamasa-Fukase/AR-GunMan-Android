package com.ar_gunman_android.domain.entities.ranking

data class RankingItem(
    val score: Double,
    val userName: String,
)

data class Ranking private constructor(
    val items: List<RankingItem>
) {
    companion object {
        operator fun invoke(items: List<RankingItem>): Ranking {
            val sortedItems = items.sortedByDescending { it.score }
            return Ranking(sortedItems)
        }
    }

    fun getTentativeRankIndex(score: Double): Int {
        // 引数のスコアと同じ or 引数のスコアよりも小さい最初の要素のindexを取得
        val tentativeRankIndex = items.indexOfFirst { it.score <= score }
        return if (tentativeRankIndex != -1) {
            tentativeRankIndex
        } else {
            // 上記の条件を満たす要素が存在しない場合は最下位ということなので、itemsのcountを返す
            items.size
        }
    }

    internal fun insertRegisteredRanking(item: RankingItem): Ranking {
        val tentativeRankIndex = getTentativeRankIndex(score = item.score)
        val updatedItems = items.toMutableList().apply { this.add(index = tentativeRankIndex, element = item) }
        return this.copy(items = updatedItems)
    }
}