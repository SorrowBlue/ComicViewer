package com.sorrowblue.comicviewer.data.datastore.serializer

import androidx.datastore.core.okio.OkioSerializer
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.protobuf.ProtoBuf
import okio.BufferedSink
import okio.BufferedSource

@OptIn(ExperimentalSerializationApi::class)
internal abstract class OkioKSerializer<T>(
    private val serializer: KSerializer<T>,
    private val coroutineDispatcher: CoroutineDispatcher = Dispatchers.IO,
) : OkioSerializer<T> {

    abstract val fileName: String

    override suspend fun readFrom(source: BufferedSource) = withContext(coroutineDispatcher) {
        ProtoBuf.decodeFromByteArray(serializer, source.readByteArray())
    }

    override suspend fun writeTo(t: T, sink: BufferedSink) {
        withContext(coroutineDispatcher) {
            sink.write(ProtoBuf.encodeToByteArray(serializer, t))
        }
    }
}
