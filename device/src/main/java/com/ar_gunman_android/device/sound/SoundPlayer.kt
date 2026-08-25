package com.ar_gunman_android.device.sound

import android.content.Context
import android.media.MediaPlayer

interface SoundPlayerInterfaces {
    fun play(sound: SoundType)
}

class SoundPlayer(context: Context) : SoundPlayerInterfaces {
    private lateinit var mediaPlayer: MediaPlayer
    private val myContext: Context = context

    override fun play(sound: SoundType) {
        mediaPlayer = MediaPlayer.create(myContext, sound.resId)
        mediaPlayer.isLooping = false
        mediaPlayer.start()
    }
}