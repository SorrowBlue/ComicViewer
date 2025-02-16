package com.sorrowblue.comicviewer.data.coil.favorite

import coil3.key.Keyer
import coil3.request.Options
import com.sorrowblue.comicviewer.domain.model.favorite.Favorite

internal object FavoriteKeyer : Keyer<Favorite> {
    override fun key(data: Favorite, options: Options) =
        "favorite:${data.id.value}"
}
