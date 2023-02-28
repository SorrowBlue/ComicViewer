package com.sorrowblue.comicviewer.data.coil.meta

import com.sorrowblue.comicviewer.domain.entity.bookshelf.BookshelfId
import java.io.OutputStream
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.encodeToByteArray
import kotlinx.serialization.protobuf.ProtoBuf
import okio.BufferedSource

@Serializable
data class BookThumbnailMetaData(
    val bookshelfId: BookshelfId = BookshelfId(0),
    val path: String = "",
    val comicFileSize: Long = 0
) {
    @OptIn(ExperimentalSerializationApi::class)
    fun write(output: OutputStream) {
        output.write(ProtoBuf.encodeToByteArray(this))
    }
}

fun readBookThumbnailMetaData(bufferedSource: BufferedSource) =
    runCatching {
        ProtoBuf.decodeFromByteArray<BookThumbnailMetaData>(bufferedSource.readByteArray())
//        Json.decodeFromString<BookThumbnailMetaData>(bufferedSource.readUtf8())
    }.getOrElse { BookThumbnailMetaData() }
