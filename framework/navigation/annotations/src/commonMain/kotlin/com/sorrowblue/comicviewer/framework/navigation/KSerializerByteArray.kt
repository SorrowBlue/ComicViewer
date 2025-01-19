package com.sorrowblue.comicviewer.framework.navigation

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.okio.decodeFromBufferedSource
import kotlinx.serialization.json.okio.encodeToBufferedSink
import kotlinx.serialization.serializer
import okio.Buffer
import okio.use

inline fun <reified T> kSerializerType(json: Json = Json) =
    KSerializerByteArray(json.serializersModule.serializer<T>())

@OptIn(ExperimentalSerializationApi::class)
class KSerializerByteArray<T>(val serializer: KSerializer<T>) {

    fun toByteArray(value: T): ByteArray =
        Buffer().also { Json.encodeToBufferedSink(serializer, value, it) }.use {
            it.readByteArray()
        }

    fun fromByteArray(bytes: ByteArray): T =
        Buffer().use {
            Json.decodeFromBufferedSource(serializer, it.write(bytes))
        }
}
