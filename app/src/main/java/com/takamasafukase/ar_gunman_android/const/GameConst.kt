package com.takamasafukase.ar_gunman_android.const

class GameConst {
    // companion objectはstatic的な役割を果たす
    companion object {
        // TODO: 余裕があればUnity側に的の生成指示をAndroid側から送る様にして、引数でこのcountを渡し仕組みに変える。Android側から色々設定を変更できる様にする。
        const val targetCount = 50
        const val timeCount = 30.00
        const val timerUpdateInterval: Long = 10
    }
}