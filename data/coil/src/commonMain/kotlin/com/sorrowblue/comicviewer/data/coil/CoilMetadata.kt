package com.sorrowblue.comicviewer.data.coil

import kotlinx.serialization.json.Json
import okio.BufferedSink
import okio.BufferedSource

internal interface CoilMetadata {

    fun writeTo(bufferedSink: BufferedSink)

    companion object {
        inline fun <reified T : CoilMetadata> from(bufferedSource: BufferedSource) =
            Json.decodeFromString<T>(bufferedSource.readUtf8())
    }
}
