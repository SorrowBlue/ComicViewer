package com.sorrowblue.comicviewer.feature.library.dropbox.data

import java.io.InputStream
import java.io.OutputStream
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoBuf

@Serializable
internal data class DropboxCredential(val credential: String? = null) {

    @ExperimentalSerializationApi
    internal class Serializer(private val coroutineDispatcher: CoroutineDispatcher = Dispatchers.IO) :
        androidx.datastore.core.Serializer<DropboxCredential> {

        override val defaultValue = DropboxCredential()

        override suspend fun readFrom(input: InputStream): DropboxCredential {
            return ProtoBuf.decodeFromByteArray(serializer(), input.readBytes())
        }

        override suspend fun writeTo(t: DropboxCredential, output: OutputStream) {
            withContext(coroutineDispatcher) {
                @Suppress("BlockingMethodInNonBlockingContext")
                output.write(ProtoBuf.encodeToByteArray(serializer(), t))
            }
        }
    }
}
