package com.sorrowblue.comicviewer.domain.entity

import com.sorrowblue.comicviewer.domain.entity.bookshelf.Bookshelf
import com.sorrowblue.comicviewer.domain.entity.file.Folder

@JvmInline
value class BookshelfFolder(val value: Pair<Bookshelf, Folder>) {
    val bookshelf get() = value.first
    val folder get() = value.second
}
