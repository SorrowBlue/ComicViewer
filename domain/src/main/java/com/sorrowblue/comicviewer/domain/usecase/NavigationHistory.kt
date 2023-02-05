package com.sorrowblue.comicviewer.domain.usecase

import com.sorrowblue.comicviewer.domain.entity.file.Folder
import com.sorrowblue.comicviewer.domain.entity.server.Server

@JvmInline
value class NavigationHistory(val triple: Triple<Server, List<Folder>, Int>) {
    constructor(server: Server, bookshelves: List<Folder>, position: Int) :
            this(Triple(server, bookshelves, position))

    val server get() = triple.first
    val bookshelves get() = triple.second
    val position get() = triple.third
}
