package com.sorrowblue.comicviewer.data.coil.favorite

import com.sorrowblue.comicviewer.data.coil.CoilMetaData
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToByteArray
import kotlinx.serialization.protobuf.ProtoBuf
import okio.BufferedSink

@OptIn(ExperimentalSerializationApi::class)
@Serializable
internal data class FavoriteThumbnailMetadata(
    val favoriteModelId: Int,
    val thumbnails: List<String>,
) : CoilMetaData {
    companion object : CoilMetaData.CompanionObject

    override fun writeTo(sink: BufferedSink) {
        sink.write(ProtoBuf.encodeToByteArray(this))
    }
}
