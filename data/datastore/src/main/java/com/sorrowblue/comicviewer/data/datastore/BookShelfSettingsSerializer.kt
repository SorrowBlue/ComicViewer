package com.sorrowblue.comicviewer.data.datastore

import androidx.datastore.core.Serializer
import com.sorrowblue.comicviewer.domain.model.settings.BookshelfSettings
import java.io.InputStream
import java.io.OutputStream
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.encodeToByteArray
import kotlinx.serialization.protobuf.ProtoBuf

@OptIn(ExperimentalSerializationApi::class)
internal object BookShelfSettingsSerializer : Serializer<BookshelfSettings> {
    override val defaultValue = BookshelfSettings()
    override suspend fun readFrom(input: InputStream): BookshelfSettings {
        return ProtoBuf.decodeFromByteArray(input.readBytes())
    }

    override suspend fun writeTo(t: BookshelfSettings, output: OutputStream) {
        output.write(ProtoBuf.encodeToByteArray(t))
    }
}
