package com.sorrowblue.comicviewer.domain.request

import com.sorrowblue.comicviewer.domain.entity.file.Book

@JvmInline
value class BookPageRequest(val value: Pair<Book, Int>) {
    val book get() = value.first
    val pageIndex get() = value.second
}
