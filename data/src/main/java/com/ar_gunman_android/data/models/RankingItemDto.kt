package com.ar_gunman_android.data.models

import com.ar_gunman_android.domain.entities.ranking.RankingItem
import com.google.firebase.firestore.PropertyName

data class RankingItemDto(
    @get:PropertyName("user_name")
    @field:PropertyName("user_name")
    val userName: String = "",

    val score: Double = 0.0,
) {
    fun toDomain(): RankingItem {
        return RankingItem(
            userName = userName,
            score = score,
        )
    }
}
