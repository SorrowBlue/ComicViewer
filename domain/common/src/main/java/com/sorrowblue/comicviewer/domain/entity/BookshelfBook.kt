package com.sorrowblue.comicviewer.domain.entity

import com.sorrowblue.comicviewer.domain.entity.bookshelf.Bookshelf
import com.sorrowblue.comicviewer.domain.entity.file.Book

@JvmInline
value class BookshelfBook(val value: Pair<Bookshelf, Book>) {
    val bookshelf get() = value.first
    val book get() = value.second
}
