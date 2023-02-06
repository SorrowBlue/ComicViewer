package com.sorrowblue.comicviewer.domain.entity

import com.sorrowblue.comicviewer.domain.entity.file.Folder
import com.sorrowblue.comicviewer.domain.entity.server.Bookshelf

@JvmInline
value class BookshelfFolder(val value: Pair<Bookshelf, Folder>) {
    val bookshelf get() = value.first
    val folder get() = value.second
}
