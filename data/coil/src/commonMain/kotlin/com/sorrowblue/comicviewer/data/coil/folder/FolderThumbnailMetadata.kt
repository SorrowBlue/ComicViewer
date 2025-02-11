package com.sorrowblue.comicviewer.data.coil.folder

import com.sorrowblue.comicviewer.data.coil.CoilMetaData
import kotlinx.io.Sink
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToByteArray
import kotlinx.serialization.protobuf.ProtoBuf

@Serializable
internal data class FolderThumbnailMetadata(
    val path: String,
    val bookshelfId: Int,
    val lastModifier: Long,
    val thumbnails: String?,
) : CoilMetaData {

    @OptIn(ExperimentalSerializationApi::class)
    override fun writeTo(sink: Sink) {
        sink.write(ProtoBuf.encodeToByteArray(this))
    }

    companion object : CoilMetaData.CompanionObject
}
