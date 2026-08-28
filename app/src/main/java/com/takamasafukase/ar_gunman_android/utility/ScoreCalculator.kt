package com.takamasafukase.ar_gunman_android.utility

import kotlin.math.min
import kotlin.random.Random

class ScoreCalculator {
    fun getTotalScore(currentScore: Double, weaponType: WeaponType): Double {
        val totalScore = min(currentScore + weaponType.hitPoint, 100.0)
        val randomMultiplier = Random.nextDouble(from = 0.9, until = 1.0)
        // ランキングがばらける様にスコアに乱数をかけて調整する
        return totalScore * randomMultiplier
    }
}