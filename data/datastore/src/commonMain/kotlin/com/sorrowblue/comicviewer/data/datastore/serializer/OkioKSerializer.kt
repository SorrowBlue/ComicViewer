/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.data.datastore.serializer

import androidx.datastore.core.okio.OkioSerializer
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.protobuf.ProtoBuf
import kotlinx.serialization.serializer
import okio.BufferedSink
import okio.BufferedSource

@OptIn(ExperimentalSerializationApi::class)
internal open class OkioKSerializer<T>(
    private val serializer: KSerializer<T>,
    val fileName: String,
    override val defaultValue: T,
) : OkioSerializer<T> {

    override suspend fun readFrom(source: BufferedSource): T = runCatching {
        ProtoBuf.decodeFromByteArray(serializer, source.readByteArray())
    }.getOrElse { defaultValue }

    override suspend fun writeTo(t: T, sink: BufferedSink) {
        sink.write(ProtoBuf.encodeToByteArray(serializer, t))
    }
}

internal inline fun <reified T> okioKSerializer(
    fileName: String,
    defaultValue: T,
): OkioKSerializer<T> = OkioKSerializer(
    serializer = serializer(),
    fileName = fileName,
    defaultValue = defaultValue,
)
