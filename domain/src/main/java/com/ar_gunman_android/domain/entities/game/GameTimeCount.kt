package com.ar_gunman_android.domain.entities.game

import kotlin.math.max

data class GameTimeCount(
    val countMillisec: Int = 30000
) {
    companion object {
        const val updateIntervalMillisec: Int = 10
    }

    val isTimeUp: Boolean
        get() = countMillisec <= 0

    internal fun decrement(): GameTimeCount {
        return this.copy(countMillisec = max(0, countMillisec - updateIntervalMillisec))
    }
}
