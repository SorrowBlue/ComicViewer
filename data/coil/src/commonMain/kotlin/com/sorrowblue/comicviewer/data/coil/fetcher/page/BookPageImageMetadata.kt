package com.sorrowblue.comicviewer.data.coil.fetcher.page

import com.sorrowblue.comicviewer.data.coil.fetcher.CoilMetadata
import com.sorrowblue.comicviewer.domain.model.BookPageImage
import kotlinx.io.Sink
import kotlinx.io.writeString
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
internal data class BookPageImageMetadata(
    val pageIndex: Int,
    val fileName: String = "",
    val fileSize: Long = 0,
) : CoilMetadata {
    constructor(data: BookPageImage) : this(data.pageIndex, data.book.name, data.book.size)

    override fun writeTo(sink: Sink) {
        sink.writeString(Json.encodeToString(this))
    }
}
