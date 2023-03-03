package com.sorrowblue.comicviewer.data.coil.folder

import com.sorrowblue.comicviewer.domain.entity.bookshelf.BookshelfId
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.encodeToByteArray
import kotlinx.serialization.protobuf.ProtoBuf
import okio.BufferedSink
import okio.BufferedSource

@Serializable
internal data class FolderThumbnailMetadata(
    val path: String,
    val bookshelfId: Int,
    val lastModifier: Long,
    val thumbnails: List<String>,
) {
    companion object {
        fun from(source: BufferedSource) =
            ProtoBuf.decodeFromByteArray<FolderThumbnailMetadata>(source.readByteArray())
    }

    fun writeTo(sink: BufferedSink) {
        sink.write(ProtoBuf.encodeToByteArray(this))
    }
}
