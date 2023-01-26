package com.sorrowblue.comicviewer.domain.usecase

import com.sorrowblue.comicviewer.domain.entity.file.Bookshelf
import com.sorrowblue.comicviewer.domain.entity.server.Server

@JvmInline
value class NavigationHistory(val triple: Triple<Server, List<Bookshelf>, Int>) {
    constructor(server: Server, bookshelves: List<Bookshelf>, position: Int) :
            this(Triple(server, bookshelves, position))

    val server get() = triple.first
    val bookshelves get() = triple.second
    val position get() = triple.third
}
