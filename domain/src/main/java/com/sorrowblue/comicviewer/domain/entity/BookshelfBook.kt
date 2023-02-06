package com.sorrowblue.comicviewer.domain.entity

import com.sorrowblue.comicviewer.domain.entity.file.Book
import com.sorrowblue.comicviewer.domain.entity.server.Bookshelf

@JvmInline
value class BookshelfBook(val value: Pair<Bookshelf, Book>) {
    val bookshelf get() = value.first
    val book get() = value.second
}
