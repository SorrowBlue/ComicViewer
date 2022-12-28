package com.sorrowblue.comicviewer.domain.entity

import com.sorrowblue.comicviewer.domain.entity.Book
import com.sorrowblue.comicviewer.domain.entity.Server

@JvmInline
value class BookPageRequest(val value: Triple<Server, Book, Int>) {
    val server get() = value.first
    val book get() = value.second
    val pageIndex get() = value.third
}
