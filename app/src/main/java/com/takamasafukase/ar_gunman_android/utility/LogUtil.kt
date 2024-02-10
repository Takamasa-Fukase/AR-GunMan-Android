package com.takamasafukase.ar_gunman_android.utility

import android.util.Log

class LogUtil {
    companion object {
        fun print(text: String) {
            Log.d("Android", "ログAndroid: $text")
        }
    }
}