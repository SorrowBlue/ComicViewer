package com.sorrowblue.comicviewer.data.mapper

import com.sorrowblue.comicviewer.data.common.favorite.FavoriteModel
import com.sorrowblue.comicviewer.domain.model.favorite.Favorite
import com.sorrowblue.comicviewer.domain.model.favorite.FavoriteId


fun FavoriteModel.toFavorite(): Favorite {
    return Favorite(FavoriteId(id.value), name, count)
}
