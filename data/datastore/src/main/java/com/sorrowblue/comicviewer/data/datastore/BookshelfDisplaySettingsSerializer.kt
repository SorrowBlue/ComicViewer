package com.sorrowblue.comicviewer.data.datastore

import androidx.datastore.core.Serializer
import com.sorrowblue.comicviewer.domain.model.settings.BookshelfDisplaySettings
import java.io.InputStream
import java.io.OutputStream
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.encodeToByteArray
import kotlinx.serialization.protobuf.ProtoBuf

@OptIn(ExperimentalSerializationApi::class)
internal object BookshelfDisplaySettingsSerializer : Serializer<BookshelfDisplaySettings> {
    override val defaultValue = BookshelfDisplaySettings()
    override suspend fun readFrom(input: InputStream): BookshelfDisplaySettings {
        return ProtoBuf.decodeFromByteArray(input.readBytes())
    }

    override suspend fun writeTo(t: BookshelfDisplaySettings, output: OutputStream) {
        output.write(ProtoBuf.encodeToByteArray(t))
    }
}
