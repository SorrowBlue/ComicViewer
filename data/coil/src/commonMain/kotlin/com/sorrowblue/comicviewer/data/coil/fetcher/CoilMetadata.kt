package com.sorrowblue.comicviewer.data.coil.fetcher

import kotlinx.io.Sink
import kotlinx.io.Source
import kotlinx.io.readString
import kotlinx.serialization.json.Json

internal interface CoilMetadata {
    fun writeTo(sink: Sink)

    companion object {
        inline fun <reified T : CoilMetadata> from(source: Source) =
            Json.decodeFromString<T>(source.readString())
    }
}
