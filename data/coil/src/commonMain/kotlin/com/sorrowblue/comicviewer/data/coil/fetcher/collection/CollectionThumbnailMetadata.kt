package com.sorrowblue.comicviewer.data.coil.fetcher.collection

import com.sorrowblue.comicviewer.data.coil.fetcher.CoilMetadata
import com.sorrowblue.comicviewer.domain.model.collection.CollectionId
import kotlinx.io.Sink
import kotlinx.io.writeString
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
internal data class CollectionThumbnailMetadata(
    val collectionId: CollectionId,
    val thumbnails: String?,
) : CoilMetadata {
    override fun writeTo(sink: Sink) {
        sink.writeString(Json.encodeToString(this))
    }
}
