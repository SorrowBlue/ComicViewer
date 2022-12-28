package com.sorrowblue.comicviewer.data.datastore

import androidx.datastore.core.Serializer
import com.sorrowblue.comicviewer.domain.model.ViewerOperationSettings
import java.io.InputStream
import java.io.OutputStream
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.encodeToByteArray
import kotlinx.serialization.protobuf.ProtoBuf

@OptIn(ExperimentalSerializationApi::class)
internal object ViewerOperationSettingsSerializer : Serializer<ViewerOperationSettings> {
    override val defaultValue = ViewerOperationSettings()
    override suspend fun readFrom(input: InputStream): ViewerOperationSettings {
        return ProtoBuf.decodeFromByteArray(input.readBytes())
    }

    override suspend fun writeTo(t: ViewerOperationSettings, output: OutputStream) {
        output.write(ProtoBuf.encodeToByteArray(t))
    }
}
