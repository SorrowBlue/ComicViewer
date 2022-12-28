package com.sorrowblue.comicviewer.data.datastore

import androidx.datastore.core.Serializer
import com.sorrowblue.comicviewer.domain.model.History
import java.io.InputStream
import java.io.OutputStream
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.encodeToByteArray
import kotlinx.serialization.protobuf.ProtoBuf

@OptIn(ExperimentalSerializationApi::class)
internal object HistorySerializer : Serializer<History> {
    override val defaultValue: History = History()
    override suspend fun readFrom(input: InputStream): History {
        return ProtoBuf.decodeFromByteArray(input.readBytes())
    }

    override suspend fun writeTo(t: History, output: OutputStream) {
        output.write(ProtoBuf.encodeToByteArray(t))
    }
}
