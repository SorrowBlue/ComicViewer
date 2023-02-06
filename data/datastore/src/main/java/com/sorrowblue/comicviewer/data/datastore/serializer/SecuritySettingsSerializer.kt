package com.sorrowblue.comicviewer.data.datastore.serializer

import androidx.datastore.core.Serializer
import com.sorrowblue.comicviewer.domain.entity.settings.SecuritySettings
import java.io.InputStream
import java.io.OutputStream
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.protobuf.ProtoBuf

@ExperimentalSerializationApi
internal class SecuritySettingsSerializer(private val coroutineDispatcher: CoroutineDispatcher = Dispatchers.IO) :
    Serializer<SecuritySettings> {
    override val defaultValue = SecuritySettings()
    override suspend fun readFrom(input: InputStream): SecuritySettings {
        return ProtoBuf.decodeFromByteArray(SecuritySettings.serializer(), input.readBytes())
    }

    override suspend fun writeTo(t: SecuritySettings, output: OutputStream) {
        withContext(coroutineDispatcher) {
            @Suppress("BlockingMethodInNonBlockingContext")
            output.write(ProtoBuf.encodeToByteArray(SecuritySettings.serializer(), t))
        }
    }
}
