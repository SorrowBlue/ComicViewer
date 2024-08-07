package com.sorrowblue.comicviewer.data.database.entity

import androidx.room.Embedded
import com.sorrowblue.comicviewer.domain.model.favorite.Favorite

internal data class FavoriteFileCountEntity(
    @Embedded val favoriteEntity: FavoriteEntity,
    val count: Int,
    val exist: Boolean,
) {

    fun toModel(): Favorite {
        return Favorite(
            id = favoriteEntity.id,
            name = favoriteEntity.name,
            count = count,
            exist = exist,
            addedDateTime = favoriteEntity.addedDateTime
        )
    }
}
