package com.sorrowblue.comicviewer.data.datastore.serializer

import androidx.datastore.core.Serializer
import com.sorrowblue.comicviewer.domain.model.settings.ViewerOperationSettings
import java.io.InputStream
import java.io.OutputStream
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.protobuf.ProtoBuf

@ExperimentalSerializationApi
internal class ViewerOperationSettingsSerializer(
    private val coroutineDispatcher: CoroutineDispatcher = Dispatchers.IO,
) :
    Serializer<ViewerOperationSettings> {
    override val defaultValue = ViewerOperationSettings()
    override suspend fun readFrom(input: InputStream): ViewerOperationSettings {
        return ProtoBuf.decodeFromByteArray(ViewerOperationSettings.serializer(), input.readBytes())
    }

    override suspend fun writeTo(t: ViewerOperationSettings, output: OutputStream) {
        withContext(coroutineDispatcher) {
            @Suppress("BlockingMethodInNonBlockingContext")
            output.write(ProtoBuf.encodeToByteArray(ViewerOperationSettings.serializer(), t))
        }
    }
}
