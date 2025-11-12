package com.sorrowblue.comicviewer.data.coil.book

import com.sorrowblue.comicviewer.data.coil.CoilMetadata
import com.sorrowblue.comicviewer.domain.model.file.BookThumbnail
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okio.BufferedSink

@Serializable
internal data class BookThumbnailMetadata(
    val path: String,
    val bookshelfId: Int,
    val lastModifier: Long,
    val size: Long,
) : CoilMetadata {
    constructor(book: BookThumbnail) : this(
        book.path,
        book.bookshelfId.value,
        book.lastModifier,
        book.size,
    )

    override fun writeTo(bufferedSink: BufferedSink) {
        bufferedSink.writeUtf8(Json.encodeToString(this))
    }
}
