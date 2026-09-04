package com.ar_gunman_android.arshootingengine.models

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.Serializer
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializer(forClass = AndroidToUnityMessageEventType::class)
object AndroidToUnityMessageEventTypeSerializer : KSerializer<AndroidToUnityMessageEventType> {
    override fun serialize(encoder: Encoder, value: AndroidToUnityMessageEventType) {
        encoder.encodeInt(value.ordinal)
    }

    override fun deserialize(decoder: Decoder): AndroidToUnityMessageEventType {
        return AndroidToUnityMessageEventType.values()[decoder.decodeInt()]
    }
}

@Serializer(forClass = UnityToAndroidMessageEventType::class)
object UnityToAndroidMessageEventTypeSerializer : KSerializer<UnityToAndroidMessageEventType> {
    override fun serialize(encoder: Encoder, value: UnityToAndroidMessageEventType) {
        encoder.encodeInt(value.ordinal)
    }

    override fun deserialize(decoder: Decoder): UnityToAndroidMessageEventType {
        val index = decoder.decodeInt()
        return UnityToAndroidMessageEventType.values().getOrElse(index) {
            throw SerializationException("Unknown ordinal value: $index for UnityToAndroidMessageEventType")
        }
    }
}