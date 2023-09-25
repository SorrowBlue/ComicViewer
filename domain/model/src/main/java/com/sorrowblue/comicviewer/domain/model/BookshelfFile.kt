package com.sorrowblue.comicviewer.domain.model

import com.sorrowblue.comicviewer.domain.model.bookshelf.Bookshelf
import com.sorrowblue.comicviewer.domain.model.file.File

@JvmInline
value class BookshelfFile(val value: Pair<Bookshelf, File>) {
    val bookshelf get() = value.first
    val file get() = value.second
}
