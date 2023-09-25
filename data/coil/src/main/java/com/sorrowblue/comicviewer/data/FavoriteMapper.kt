package com.sorrowblue.comicviewer.data

import coil.map.Mapper
import coil.request.Options
import com.sorrowblue.comicviewer.data.model.favorite.FavoriteModel
import com.sorrowblue.comicviewer.data.model.favorite.FavoriteModelId
import com.sorrowblue.comicviewer.domain.model.favorite.Favorite

internal class FavoriteMapper : Mapper<Favorite, FavoriteModel> {

    override fun map(data: Favorite, options: Options) =
        FavoriteModel(FavoriteModelId(data.id.value), data.name, data.count)
}
