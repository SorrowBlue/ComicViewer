package com.sorrowblue.comicviewer.data.datastore.serializer

import androidx.datastore.core.Serializer
import com.sorrowblue.comicviewer.domain.entity.settings.Settings
import java.io.InputStream
import java.io.OutputStream
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.protobuf.ProtoBuf

@ExperimentalSerializationApi
internal class SettingsSerializer(private val coroutineDispatcher: CoroutineDispatcher = Dispatchers.IO) : Serializer<Settings> {
    override val defaultValue = Settings()
    override suspend fun readFrom(input: InputStream): Settings {
        return ProtoBuf.decodeFromByteArray(Settings.serializer(), input.readBytes())
    }

    override suspend fun writeTo(t: Settings, output: OutputStream) {
        withContext(coroutineDispatcher) {
            @Suppress("BlockingMethodInNonBlockingContext")
            output.write(ProtoBuf.encodeToByteArray(Settings.serializer(), t))
        }
    }
}
