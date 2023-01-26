package com.sorrowblue.comicviewer.data.coil.meta

import java.io.OutputStream
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToStream
import okio.BufferedSource

@Serializable
data class BookThumbnailMetaData(
    val comicFileLastModified: Long,
    val comicFileSize: Long
) {
    @OptIn(ExperimentalSerializationApi::class)
    fun write(output: OutputStream) {
        Json.encodeToStream(this, output)
    }
    companion object {
        fun read(bufferedSource: BufferedSource) =
            kotlin.runCatching { Json.decodeFromString<BookThumbnailMetaData>(bufferedSource.readUtf8()) }
                .getOrElse { BookThumbnailMetaData(0, 0) }
    }
}
