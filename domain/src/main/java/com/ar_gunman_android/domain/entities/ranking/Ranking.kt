package com.ar_gunman_android.domain.entities.ranking

data class RankingItem(
    val score: Double,
    val userName: String,
)

class Ranking(
    items: List<RankingItem>
) {
    val items: List<RankingItem>

    init {
        // スコアの高い順にソート
        val sortedItems = items.sortedByDescending { it.score }
        this.items = sortedItems
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
        return Ranking(items = updatedItems)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return false
        if (other !is Ranking) return false
        return items == other.items
    }

    override fun hashCode(): Int {
        return items.hashCode()
    }
}