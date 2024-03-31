package com.sorrowblue.comicviewer.data.datastore.serializer

import androidx.datastore.core.Serializer
import com.sorrowblue.comicviewer.domain.model.settings.ViewerSettings
import java.io.InputStream
import java.io.OutputStream
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.protobuf.ProtoBuf

@ExperimentalSerializationApi
internal class ViewerSettingsSerializer(private val coroutineDispatcher: CoroutineDispatcher = Dispatchers.IO) :
    Serializer<ViewerSettings> {
    override val defaultValue = ViewerSettings()
    override suspend fun readFrom(input: InputStream): ViewerSettings {
        return ProtoBuf.decodeFromByteArray(ViewerSettings.serializer(), input.readBytes())
    }

    override suspend fun writeTo(t: ViewerSettings, output: OutputStream) {
        withContext(coroutineDispatcher) {
            output.write(ProtoBuf.encodeToByteArray(ViewerSettings.serializer(), t))
        }
    }
}
