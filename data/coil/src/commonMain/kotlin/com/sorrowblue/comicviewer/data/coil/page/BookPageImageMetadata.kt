package com.sorrowblue.comicviewer.data.coil.page

import com.sorrowblue.comicviewer.data.coil.CoilMetadata
import com.sorrowblue.comicviewer.domain.model.BookPageImage
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okio.BufferedSink

@Serializable
internal data class BookPageImageMetadata(
    val pageIndex: Int,
    val fileName: String = "",
    val fileSize: Long = 0,
) : CoilMetadata {

    constructor(data: BookPageImage) : this(data.pageIndex, data.book.name, data.book.size)

    override fun writeTo(bufferedSink: BufferedSink) {
        bufferedSink.writeUtf8(Json.encodeToString(this))
    }
}
