package com.sorrowblue.comicviewer.framework.ui

import androidx.compose.runtime.saveable.SaverScope
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.okio.decodeFromBufferedSource
import kotlinx.serialization.json.okio.encodeToBufferedSink
import okio.Buffer
import okio.BufferedSource

@OptIn(ExperimentalSerializationApi::class)
abstract class KSerializableSaver<T>(private val serializer: KSerializer<T>) :
    androidx.compose.runtime.saveable.Saver<T, ByteArray> {
    override fun restore(value: ByteArray): T {
        return Json.decodeFromBufferedSource(serializer, byteArrayToBufferedSource(value))
    }

    override fun SaverScope.save(value: T): ByteArray {
        return Buffer().also {
            Json.encodeToBufferedSink(serializer, value, it)
        }.readByteArray()
    }
}

fun byteArrayToBufferedSource(byteArray: ByteArray): BufferedSource {
    val buffer = Buffer()
    buffer.write(byteArray)
    return buffer.peek()
}
