package com.sorrowblue.comicviewer.data.coil.book

import com.sorrowblue.comicviewer.data.coil.CoilMetaData
import com.sorrowblue.comicviewer.domain.model.file.Book
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.encodeToByteArray
import kotlinx.serialization.protobuf.ProtoBuf
import okio.BufferedSink
import okio.BufferedSource

@OptIn(ExperimentalSerializationApi::class)
@Serializable
internal data class BookThumbnailMetadata(
    val path: String,
    val bookshelfId: Int,
    val lastModifier: Long,
    val size: Long,
) : CoilMetaData {

    constructor(book: Book) : this(
        book.path,
        book.bookshelfId.value,
        book.lastModifier,
        book.size
    )

    override fun writeTo(sink: BufferedSink) {
        sink.write(ProtoBuf.encodeToByteArray(this))
    }

    companion object {
        fun from(source: BufferedSource) =
            ProtoBuf.decodeFromByteArray<BookThumbnailMetadata>(source.readByteArray())
    }
}
