package com.sorrowblue.comicviewer.feature.search

import com.ramcosta.composedestinations.navargs.DestinationsNavTypeSerializer
import com.ramcosta.composedestinations.navargs.NavTypeSerializer
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId

@NavTypeSerializer
internal class BookshelfIdSerializer : DestinationsNavTypeSerializer<BookshelfId> {
    override fun toRouteString(value: BookshelfId) = value.value.toString()
    override fun fromRouteString(routeStr: String) = BookshelfId(routeStr.toInt())
}
