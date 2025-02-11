package com.sorrowblue.comicviewer.data.coil.book

import com.sorrowblue.comicviewer.data.coil.CoilMetaData
import com.sorrowblue.comicviewer.domain.model.file.BookThumbnail
import kotlinx.io.Sink
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToByteArray
import kotlinx.serialization.protobuf.ProtoBuf

@OptIn(ExperimentalSerializationApi::class)
@Serializable
internal data class BookThumbnailMetadata(
    val path: String,
    val bookshelfId: Int,
    val lastModifier: Long,
    val size: Long,
) : CoilMetaData {

    constructor(book: BookThumbnail) : this(
        book.path,
        book.bookshelfId.value,
        book.lastModifier,
        book.size
    )

    override fun writeTo(sink: Sink) {
        sink.write(ProtoBuf.encodeToByteArray(this))
    }

    companion object : CoilMetaData.CompanionObject
}
