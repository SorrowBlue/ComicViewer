package com.sorrowblue.comicviewer.domain.model

import com.sorrowblue.comicviewer.domain.model.bookshelf.Bookshelf
import com.sorrowblue.comicviewer.domain.model.file.IFolder

@JvmInline
value class BookshelfFolder(val value: Pair<Bookshelf, IFolder>) {
    val bookshelf get() = value.first
    val folder get() = value.second
}
