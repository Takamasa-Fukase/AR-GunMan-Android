package com.takamasafukase.ar_gunman_android.utility

import com.takamasafukase.ar_gunman_android.const.GameConst
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.isActive
import kotlin.math.max

class TimeCountUtil {
    // RxSwiftのObservable<Int>.interval的なものをFlowで生成して返却
    fun createFlowTimer(interval: Long): Flow<Double> = flow {
        // 30秒のミリ秒
        val timerDuration = GameConst.timeCount * 1000L
        // 開始時刻
        val startTime = System.nanoTime()

        // コルーチンがアクティブな間ループ
        while (currentCoroutineContext().isActive) {
            val elapsedMillis = java.util.concurrent.TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime)
            // 0よりは下回らない様にする
            val remainingTime = max((timerDuration) - (elapsedMillis.toDouble()), 0.00) / 1000.0
            emit(remainingTime)
            delay(interval)
        }
    }

    // 少数2桁表示のテキストに変換して返却（少数じゃない部分が1桁の場合は0埋めする）
    fun getTwoDigitTimeCountText(timeCount: Double) : String {
        val strTimeCount = String.format("%.2f", timeCount)
        return if (timeCount >= 10) strTimeCount else "0$strTimeCount"
    }
}