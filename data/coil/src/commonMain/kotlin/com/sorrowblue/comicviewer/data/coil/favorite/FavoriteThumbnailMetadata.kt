package com.sorrowblue.comicviewer.data.coil.favorite

import com.sorrowblue.comicviewer.data.coil.CoilMetadata
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okio.BufferedSink

@Serializable
internal data class FavoriteThumbnailMetadata(
    val favoriteModelId: Int,
    val thumbnails: String?,
) : CoilMetadata {

    override fun writeTo(bufferedSink: BufferedSink) {
        bufferedSink.writeUtf8(Json.encodeToString(this))
    }
}
