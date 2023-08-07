package com.sorrowblue.comicviewer.data.database.entity

import androidx.room.Embedded
import com.sorrowblue.comicviewer.data.common.favorite.FavoriteModel
import com.sorrowblue.comicviewer.data.common.favorite.FavoriteModelId

internal data class FavoriteFileCount(
    @Embedded val favorite: Favorite,
    val count: Int
) {

    fun toModel(): FavoriteModel {
        return FavoriteModel(FavoriteModelId(favorite.id), favorite.name, count)
    }
}
