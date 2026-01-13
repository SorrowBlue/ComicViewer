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
