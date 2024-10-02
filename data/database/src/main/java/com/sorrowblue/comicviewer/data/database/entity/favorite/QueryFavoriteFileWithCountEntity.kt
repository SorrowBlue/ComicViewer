package com.sorrowblue.comicviewer.data.database.entity.favorite

import androidx.room.Embedded
import com.sorrowblue.comicviewer.domain.model.favorite.Favorite

internal class QueryFavoriteFileWithCountEntity(
    @Embedded val favoriteEntity: FavoriteEntity,
    private val count: Int,
    private val exist: Boolean,
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
