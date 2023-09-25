package com.sorrowblue.comicviewer.data.datastore.serializer

import androidx.datastore.core.Serializer
import com.sorrowblue.comicviewer.domain.model.settings.DisplaySettings
import java.io.InputStream
import java.io.OutputStream
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.protobuf.ProtoBuf

@ExperimentalSerializationApi
internal class DisplaySettingsSerializer(private val coroutineDispatcher: CoroutineDispatcher = Dispatchers.IO) :
    Serializer<DisplaySettings> {
    override val defaultValue = DisplaySettings()
    override suspend fun readFrom(input: InputStream): DisplaySettings {
        return ProtoBuf.decodeFromByteArray(DisplaySettings.serializer(), input.readBytes())
    }

    override suspend fun writeTo(t: DisplaySettings, output: OutputStream) {
        withContext(coroutineDispatcher) {
            @Suppress("BlockingMethodInNonBlockingContext")
            output.write(ProtoBuf.encodeToByteArray(DisplaySettings.serializer(), t))
        }
    }
}
