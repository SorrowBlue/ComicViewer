package com.sorrowblue.comicviewer.domain.entity

import com.sorrowblue.comicviewer.domain.entity.bookshelf.Bookshelf
import com.sorrowblue.comicviewer.domain.entity.file.IFolder

@JvmInline
value class BookshelfFolder(val value: Pair<Bookshelf, IFolder>) {
    val bookshelf get() = value.first
    val folder get() = value.second
}
