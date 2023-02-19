package com.sorrowblue.comicviewer.library.box.data

import java.io.InputStream
import java.io.OutputStream
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoBuf

@Serializable
data class BoxConnectionState(val state: String? = null) {

    @ExperimentalSerializationApi
    internal class Serializer(private val coroutineDispatcher: CoroutineDispatcher = Dispatchers.IO) :
        androidx.datastore.core.Serializer<BoxConnectionState> {

        override val defaultValue = BoxConnectionState()

        override suspend fun readFrom(input: InputStream): BoxConnectionState {
            return ProtoBuf.decodeFromByteArray(serializer(), input.readBytes())
        }

        override suspend fun writeTo(t: BoxConnectionState, output: OutputStream) {
            withContext(coroutineDispatcher) {
                @Suppress("BlockingMethodInNonBlockingContext")
                output.write(ProtoBuf.encodeToByteArray(serializer(), t))
            }
        }
    }
}
