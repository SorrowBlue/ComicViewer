package com.sorrowblue.comicviewer.data.datastore.serializer

import androidx.datastore.core.Serializer
import com.sorrowblue.comicviewer.domain.entity.settings.FolderDisplaySettings
import java.io.InputStream
import java.io.OutputStream
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.protobuf.ProtoBuf

@ExperimentalSerializationApi
internal class FolderDisplaySettingsSerializer(private val coroutineDispatcher: CoroutineDispatcher = Dispatchers.IO) : Serializer<FolderDisplaySettings> {

    override val defaultValue = FolderDisplaySettings()

    override suspend fun readFrom(input: InputStream): FolderDisplaySettings {
        return ProtoBuf.decodeFromByteArray(FolderDisplaySettings.serializer(), input.readBytes())
    }

    override suspend fun writeTo(t: FolderDisplaySettings, output: OutputStream) {
        withContext(coroutineDispatcher) {
            @Suppress("BlockingMethodInNonBlockingContext")
            output.write(ProtoBuf.encodeToByteArray(FolderDisplaySettings.serializer(), t))
        }
    }
}
