package com.sorrowblue.comicviewer.domain.entity

import com.sorrowblue.comicviewer.domain.entity.file.Book
import com.sorrowblue.comicviewer.domain.entity.server.Server

@JvmInline
value class ServerBook(val value: Pair<Server, Book>) {
    val server get() = value.first
    val book get() = value.second
}
