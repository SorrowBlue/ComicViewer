package com.sorrowblue.comicviewer.framework.ui

import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.SaverScope
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.cbor.Cbor
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.encodeToByteArray

@OptIn(ExperimentalSerializationApi::class)
abstract class KSerializableSaver<T : Any?>(private val serializer: KSerializer<T>) :
    Saver<T, ByteArray> {
    override fun restore(value: ByteArray): T {
        return Cbor.decodeFromByteArray(serializer, value)
    }

    override fun SaverScope.save(value: T): ByteArray {
        return Cbor.encodeToByteArray(serializer, value)
    }
}

@OptIn(ExperimentalSerializationApi::class)
inline fun <reified Original : Any> kSerializableSaver(): Saver<Original, Any> {
    return object : Saver<Original, Any> {
        override fun restore(value: Any) =
            kotlin.runCatching { Cbor.decodeFromByteArray<Original>(value as ByteArray) }
                .getOrNull()

        override fun SaverScope.save(value: Original) =
            kotlin.runCatching { Cbor.encodeToByteArray(value) }.getOrNull()
    }
}

@OptIn(ExperimentalSerializationApi::class)
inline fun <reified Original : Any> kSerializableByteArraySaver(): Saver<Original, ByteArray> {
    return object : Saver<Original, ByteArray> {
        override fun restore(value: ByteArray): Original? {
            return kotlin.runCatching { Cbor.decodeFromByteArray<Original>(value) }.getOrNull()
        }

        override fun SaverScope.save(value: Original): ByteArray? {
            return kotlin.runCatching { Cbor.encodeToByteArray(value) }.getOrNull()
        }
    }
}
