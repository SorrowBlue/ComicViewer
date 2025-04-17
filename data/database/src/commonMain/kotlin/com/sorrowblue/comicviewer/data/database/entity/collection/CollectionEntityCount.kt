package com.sorrowblue.comicviewer.data.database.entity.collection

import androidx.room.Embedded
import com.sorrowblue.comicviewer.domain.model.collection.Collection

internal class CollectionEntityCount(
    @Embedded val entity: CollectionEntity,
    private val count: Int,
) {

    fun toModel(): Collection {
        return entity.toModel(count)
    }
}

internal class CollectionEntityCountExist(
    @Embedded val entity: CollectionEntity,
    private val count: Int,
    private val exist: Boolean,
) {

    fun toModel(): Pair<Collection, Boolean> {
        return entity.toModel(count) to exist
    }
}
