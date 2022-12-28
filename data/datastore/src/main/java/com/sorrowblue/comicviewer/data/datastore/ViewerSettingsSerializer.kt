package com.sorrowblue.comicviewer.data.datastore

import androidx.datastore.core.Serializer
import com.sorrowblue.comicviewer.domain.model.ViewerSettings
import java.io.InputStream
import java.io.OutputStream
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.encodeToByteArray
import kotlinx.serialization.protobuf.ProtoBuf

@OptIn(ExperimentalSerializationApi::class)
internal object ViewerSettingsSerializer : Serializer<ViewerSettings> {
    override val defaultValue = ViewerSettings()
    override suspend fun readFrom(input: InputStream): ViewerSettings {
        return ProtoBuf.decodeFromByteArray(input.readBytes())
    }

    override suspend fun writeTo(t: ViewerSettings, output: OutputStream) {
        output.write(ProtoBuf.encodeToByteArray(t))
    }
}
