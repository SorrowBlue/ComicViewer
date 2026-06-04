package com.sorrowblue.comicviewer.data.coil.fetcher.book

import com.sorrowblue.comicviewer.data.coil.fetcher.CoilMetadata
import com.sorrowblue.comicviewer.domain.model.file.BookThumbnail
import kotlinx.io.Sink
import kotlinx.io.writeString
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

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

    override fun writeTo(sink: Sink) {
        sink.writeString(Json.encodeToString(this))
    }
}
