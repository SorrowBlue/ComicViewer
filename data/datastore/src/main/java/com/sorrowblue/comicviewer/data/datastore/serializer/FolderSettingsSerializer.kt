package com.sorrowblue.comicviewer.data.datastore.serializer

import androidx.datastore.core.Serializer
import com.sorrowblue.comicviewer.domain.model.settings.FolderSettings
import java.io.InputStream
import java.io.OutputStream
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.protobuf.ProtoBuf

@ExperimentalSerializationApi
internal class FolderSettingsSerializer(private val coroutineDispatcher: CoroutineDispatcher = Dispatchers.IO) :
    Serializer<FolderSettings> {

    override val defaultValue = FolderSettings()

    override suspend fun readFrom(input: InputStream): FolderSettings {
        return ProtoBuf.decodeFromByteArray(FolderSettings.serializer(), input.readBytes())
    }

    override suspend fun writeTo(t: FolderSettings, output: OutputStream) {
        withContext(coroutineDispatcher) {
            output.write(ProtoBuf.encodeToByteArray(FolderSettings.serializer(), t))
        }
    }
}
