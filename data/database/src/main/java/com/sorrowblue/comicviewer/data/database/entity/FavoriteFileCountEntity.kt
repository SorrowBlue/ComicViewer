package com.sorrowblue.comicviewer.data.database.entity

import androidx.room.Embedded
import com.sorrowblue.comicviewer.domain.model.favorite.Favorite
import com.sorrowblue.comicviewer.domain.model.favorite.FavoriteId

internal data class FavoriteFileCountEntity(
    @Embedded val favoriteEntity: FavoriteEntity,
    val count: Int,
) {

    fun toModel(): Favorite {
        return Favorite(
            id = FavoriteId(favoriteEntity.id),
            name = favoriteEntity.name,
            count = count
        )
    }
}
