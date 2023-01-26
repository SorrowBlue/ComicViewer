package com.sorrowblue.comicviewer.domain.entity

import com.sorrowblue.comicviewer.domain.entity.file.Bookshelf
import com.sorrowblue.comicviewer.domain.entity.server.Server

@JvmInline
value class ServerBookshelf(val value: Pair<Server, Bookshelf>) {
    val server get() = value.first
    val bookshelf get() = value.second
}
