package com.sorrowblue.comicviewer.framework.ui

import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.SaverScope
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.cbor.Cbor
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.encodeToByteArray
import logcat.logcat

@OptIn(ExperimentalSerializationApi::class)
inline fun <reified Original : Any> kSerializableSaver(): Saver<Original, Any> {
    return object : Saver<Original, Any> {
        override fun restore(value: Any): Original? {
            logcat { "restore, $value" }
            return kotlin
                .runCatching { Cbor.decodeFromByteArray<Original>(value as ByteArray) }
                .getOrNull()
        }

        override fun SaverScope.save(value: Original): ByteArray? {
            logcat { "save, $value" }
            return kotlin.runCatching { Cbor.encodeToByteArray(value) }.getOrNull()
        }
    }
}

@OptIn(ExperimentalSerializationApi::class)
inline fun <Original : Any, reified Saveable : Any> kSerializableSaver(
    crossinline save: (Original) -> Saveable,
    crossinline restore: (Saveable) -> Original,
): Saver<Original, Any> {
    return object : Saver<Original, Any> {
        override fun SaverScope.save(value: Original): ByteArray? {
            logcat { "save, $value" }
            return kotlin.runCatching { Cbor.encodeToByteArray<Saveable>(save(value)) }.getOrNull()
        }

        override fun restore(value: Any): Original? {
            logcat { "restore, $value" }
            return kotlin
                .runCatching { Cbor.decodeFromByteArray<Saveable>(value as ByteArray) }
                .getOrNull()
                ?.let { restore(it) }
        }
    }
}
