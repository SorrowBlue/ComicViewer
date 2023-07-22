package com.sorrowblue.comicviewer.data.coil.favorite

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoBuf
import okio.BufferedSink
import okio.BufferedSource

@OptIn(ExperimentalSerializationApi::class)
@Serializable
internal data class FavoriteThumbnailMetadata(
    val favoriteModelId: Int,
    val thumbnails: List<String>,
) {
    companion object {
        fun from(source: BufferedSource) =
            ProtoBuf.decodeFromByteArray(serializer(), source.readByteArray())
    }

    fun writeTo(sink: BufferedSink) {
        sink.write(ProtoBuf.encodeToByteArray(serializer(), this))
    }
}
