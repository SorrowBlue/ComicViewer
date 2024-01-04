package com.sorrowblue.comicviewer.bookshelf.navigation

import com.sorrowblue.comicviewer.bookshelf.destinations.BookshelfFolderScreenDestination
import com.sorrowblue.comicviewer.bookshelf.destinations.BookshelfScreenDestination

val routeInBookshelfGraph
    get() = listOf(
        BookshelfScreenDestination.route,
        BookshelfFolderScreenDestination.route
    )
