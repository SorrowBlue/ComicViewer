package com.sorrowblue.comicviewer.data.coil.collection

import coil3.key.Keyer
import coil3.request.Options
import com.sorrowblue.comicviewer.domain.model.collection.Collection

internal object CollectionKeyer : Keyer<Collection> {
    override fun key(data: Collection, options: Options) = "collection:${data.id.value}"
}
