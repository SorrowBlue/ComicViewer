package com.sorrowblue.comicviewer.data.datastore

import androidx.datastore.core.Serializer
import com.sorrowblue.comicviewer.domain.model.DisplaySettings
import java.io.InputStream
import java.io.OutputStream
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.encodeToByteArray
import kotlinx.serialization.protobuf.ProtoBuf

@OptIn(ExperimentalSerializationApi::class)
internal object DisplaySettingsSerializer : Serializer<DisplaySettings> {
    override val defaultValue = DisplaySettings()
    override suspend fun readFrom(input: InputStream): DisplaySettings {
        return ProtoBuf.decodeFromByteArray(input.readBytes())
    }

    override suspend fun writeTo(t: DisplaySettings, output: OutputStream) {
        output.write(ProtoBuf.encodeToByteArray(t))
    }
}
