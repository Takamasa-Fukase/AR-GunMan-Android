package com.example.domain.serviceInterfaces

import com.example.domain.entities.soundType.SoundType

interface SoundPlayerInterfaces {
    fun play(sound: SoundType)
}