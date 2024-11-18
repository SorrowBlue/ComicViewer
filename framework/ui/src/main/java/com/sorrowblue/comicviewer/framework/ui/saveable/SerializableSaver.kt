package com.sorrowblue.comicviewer.framework.ui.saveable

import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.SaverScope
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

typealias SerializableSaver<Original> = Saver<Original, String>

inline fun <reified Original : @Serializable Any> serializableSaver() =
    object : SerializableSaver<Original> {

        override fun restore(value: String) =
            kotlin.runCatching { Json.decodeFromString<Original>(value) }.getOrNull()

        override fun SaverScope.save(value: Original) =
            kotlin.runCatching { Json.Default.encodeToString(value) }.getOrNull()
    }
