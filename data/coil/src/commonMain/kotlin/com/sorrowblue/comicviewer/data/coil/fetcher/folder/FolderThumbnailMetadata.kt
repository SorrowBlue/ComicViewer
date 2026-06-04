package com.sorrowblue.comicviewer.data.coil.fetcher.folder

import com.sorrowblue.comicviewer.data.coil.fetcher.CoilMetadata
import kotlinx.io.Sink
import kotlinx.io.writeString
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
internal data class FolderThumbnailMetadata(
    val path: String,
    val bookshelfId: Int,
    val lastModifier: Long,
    val thumbnails: String?,
) : CoilMetadata {
    override fun writeTo(sink: Sink) {
        sink.writeString(Json.encodeToString(this))
    }
}
