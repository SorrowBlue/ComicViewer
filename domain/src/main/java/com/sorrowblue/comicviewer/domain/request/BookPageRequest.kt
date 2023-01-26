package com.sorrowblue.comicviewer.domain.request

import com.sorrowblue.comicviewer.domain.entity.file.Book
import com.sorrowblue.comicviewer.domain.entity.server.Server

@JvmInline
value class BookPageRequest(val value: Triple<Server, Book, Int>) {
    val server get() = value.first
    val book get() = value.second
    val pageIndex get() = value.third
}
