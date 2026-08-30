package com.takamasafukase.ar_gunman_android.extensions

val Int.timeCountText: String
    get() {
        val timeCountMillisec: Int = this

        val doubleTimeCount = timeCountMillisec.toDouble() / 1000.toDouble()

        val strTimeCount = String.format("%.2f", doubleTimeCount)

        return if (timeCountMillisec < 10000) {
            "0$strTimeCount"
        } else {
            strTimeCount
        }
    }