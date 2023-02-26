package com.sorrowblue.comicviewer.domain.entity

import com.sorrowblue.comicviewer.domain.entity.bookshelf.Bookshelf
import com.sorrowblue.comicviewer.domain.entity.file.File

@JvmInline
value class BookshelfFile(val value: Pair<Bookshelf, File>) {
    val bookshelf get() = value.first
    val file get() = value.second
}
