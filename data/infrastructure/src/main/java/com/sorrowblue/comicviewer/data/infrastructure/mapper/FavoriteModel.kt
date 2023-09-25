package com.sorrowblue.comicviewer.data.infrastructure.mapper

import com.sorrowblue.comicviewer.data.model.favorite.FavoriteModel
import com.sorrowblue.comicviewer.domain.model.favorite.Favorite
import com.sorrowblue.comicviewer.domain.model.favorite.FavoriteId


fun FavoriteModel.toFavorite(): Favorite {
    return Favorite(FavoriteId(id.value), name, count)
}
