package com.ar_gunman_android.domain.entities.game

import kotlin.math.min
import kotlin.random.Random

data class GameScore(
    val value: Double = 0.0
) {
    internal fun add(targetHitPoint: Int): GameScore {
        val randomlyAdjustedHitPoint = targetHitPoint.toDouble() * Random.nextDouble(0.9, 1.0)
        return this.copy(value = min(value + randomlyAdjustedHitPoint, 100.0))
    }
}
