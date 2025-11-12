package com.sorrowblue.comicviewer.data.coil.folder

import com.sorrowblue.comicviewer.data.coil.CoilMetadata
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okio.BufferedSink

@Serializable
internal data class FolderThumbnailMetadata(
    val path: String,
    val bookshelfId: Int,
    val lastModifier: Long,
    val thumbnails: String?,
) : CoilMetadata {
    override fun writeTo(bufferedSink: BufferedSink) {
        bufferedSink.writeUtf8(Json.encodeToString(this))
    }
}
