/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.data.datastore.serializer

import androidx.datastore.core.okio.OkioSerializer
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.protobuf.ProtoBuf
import okio.BufferedSink
import okio.BufferedSource

@OptIn(ExperimentalSerializationApi::class)
internal abstract class OkioKSerializer<T>(private val serializer: KSerializer<T>) :
    OkioSerializer<T> {
    abstract val fileName: String

    override suspend fun readFrom(source: BufferedSource): T = runCatching {
        ProtoBuf.decodeFromByteArray(serializer, source.readByteArray())
    }.getOrElse { defaultValue }

    override suspend fun writeTo(t: T, sink: BufferedSink) {
        sink.write(ProtoBuf.encodeToByteArray(serializer, t))
    }
}
