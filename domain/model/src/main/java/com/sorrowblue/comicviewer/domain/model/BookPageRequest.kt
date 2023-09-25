package com.sorrowblue.comicviewer.domain.model

import com.sorrowblue.comicviewer.domain.model.file.Book

@JvmInline
value class BookPageRequest(val value: Pair<Book, Int>) {
    val book get() = value.first
    val pageIndex get() = value.second
}
