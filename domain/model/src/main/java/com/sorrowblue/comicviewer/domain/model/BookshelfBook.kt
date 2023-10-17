package com.sorrowblue.comicviewer.domain.model

import com.sorrowblue.comicviewer.domain.model.bookshelf.Bookshelf
import com.sorrowblue.comicviewer.domain.model.file.Book

@JvmInline
value class BookshelfBook(val value: Pair<Bookshelf, Book>) {
    val bookshelf get() = value.first
    val book get() = value.second
}
