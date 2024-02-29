package com.sorrowblue.comicviewer.data.datastore.serializer

import androidx.datastore.core.Serializer
import com.sorrowblue.comicviewer.domain.model.settings.OneTimeFlag
import com.sorrowblue.comicviewer.domain.model.settings.SecuritySettings
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
            output.write(ProtoBuf.encodeToByteArray(SecuritySettings.serializer(), t))
        }
    }
}

@ExperimentalSerializationApi
internal class OneTimeFlagSerializer(private val coroutineDispatcher: CoroutineDispatcher = Dispatchers.IO) :
    Serializer<OneTimeFlag> {
    override val defaultValue = OneTimeFlag()
    override suspend fun readFrom(input: InputStream): OneTimeFlag {
        return ProtoBuf.decodeFromByteArray(OneTimeFlag.serializer(), input.readBytes())
    }

    override suspend fun writeTo(t: OneTimeFlag, output: OutputStream) {
        withContext(coroutineDispatcher) {
            output.write(ProtoBuf.encodeToByteArray(OneTimeFlag.serializer(), t))
        }
    }
}
