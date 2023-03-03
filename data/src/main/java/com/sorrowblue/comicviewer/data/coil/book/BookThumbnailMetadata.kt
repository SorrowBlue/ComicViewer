package com.sorrowblue.comicviewer.data.coil.book

import com.sorrowblue.comicviewer.data.common.FileModel
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.encodeToByteArray
import kotlinx.serialization.protobuf.ProtoBuf
import okio.BufferedSink
import okio.BufferedSource

@Serializable
internal data class BookThumbnailMetadata(
    val path: String,
    val bookshelfId: Int,
    val lastModifier: Long,
    val size: Long,
) {

    constructor(book: FileModel.Book) : this(
        book.path, book.bookshelfModelId.value, book.lastModifier, book.size
    )

    companion object {
        fun from(source: BufferedSource) =
            ProtoBuf.decodeFromByteArray<BookThumbnailMetadata>(source.readByteArray())
    }

    fun writeTo(sink: BufferedSink) {
        sink.write(ProtoBuf.encodeToByteArray(this))
    }
}
