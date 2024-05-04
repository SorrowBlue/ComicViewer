package com.sorrowblue.comicviewer.framework.ui.navigation

import com.ramcosta.composedestinations.navargs.DestinationsNavTypeSerializer
import com.ramcosta.composedestinations.navargs.NavTypeSerializer
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.favorite.FavoriteId

@NavTypeSerializer
internal class BookshelfIdSerializer : DestinationsNavTypeSerializer<BookshelfId> {
    override fun toRouteString(value: BookshelfId) = value.value.toString()
    override fun fromRouteString(routeStr: String) = BookshelfId(routeStr.toInt())
}

@NavTypeSerializer
internal class FavoriteIdSerializer : DestinationsNavTypeSerializer<FavoriteId> {
    override fun toRouteString(value: FavoriteId) = value.value.toString()
    override fun fromRouteString(routeStr: String) = FavoriteId(routeStr.toInt())
}
