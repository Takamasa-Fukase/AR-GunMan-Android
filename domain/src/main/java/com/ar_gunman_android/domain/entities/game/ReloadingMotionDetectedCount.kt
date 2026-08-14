package com.ar_gunman_android.domain.entities.game

data class ReloadingMotionDetectedCount(
    val count: Int = 0
) {
    internal fun update(): Pair<ReloadingMotionDetectedCount, ReloadingMotionDetectedCountUpdateResult> {
        val newCount = count + 1
        val result = if (newCount == 20) {
            ReloadingMotionDetectedCountUpdateResult.EXCEEDED_LIMIT
        } else {
            ReloadingMotionDetectedCountUpdateResult.NOT_EXCEEDED_LIMIT
        }
        return Pair(this.copy(count = newCount), result)
    }
}

enum class ReloadingMotionDetectedCountUpdateResult {
    NOT_EXCEEDED_LIMIT,
    EXCEEDED_LIMIT;
}