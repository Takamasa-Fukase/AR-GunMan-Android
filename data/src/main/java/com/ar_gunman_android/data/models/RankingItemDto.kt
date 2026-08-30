package com.ar_gunman_android.data.models

import com.ar_gunman_android.domain.entities.ranking.RankingItem
import com.google.firebase.firestore.PropertyName

data class RankingItemDto(
    val score: Double = 0.0,

    @get:PropertyName("user_name")
    @field:PropertyName("user_name")
    val userName: String = "",
) {
    fun toDomain(): RankingItem {
        return RankingItem(
            score = score,
            userName = userName,
        )
    }
}

fun RankingItem.toDto(): RankingItemDto {
    return RankingItemDto(
        score = score,
        userName = userName,
    )
}