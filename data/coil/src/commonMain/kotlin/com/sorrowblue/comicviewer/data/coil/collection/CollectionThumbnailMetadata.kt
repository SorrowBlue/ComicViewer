package com.sorrowblue.comicviewer.data.coil.collection

import com.sorrowblue.comicviewer.data.coil.CoilMetadata
import com.sorrowblue.comicviewer.domain.model.collection.CollectionId
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okio.BufferedSink

@Serializable
internal data class CollectionThumbnailMetadata(
    val collectionId: CollectionId,
    val thumbnails: String?,
) : CoilMetadata {

    override fun writeTo(bufferedSink: BufferedSink) {
        bufferedSink.writeUtf8(Json.encodeToString(this))
    }
}
