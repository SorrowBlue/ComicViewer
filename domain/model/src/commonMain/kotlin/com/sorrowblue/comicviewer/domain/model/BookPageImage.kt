package com.sorrowblue.comicviewer.domain.model

import com.sorrowblue.comicviewer.domain.model.file.Book
import kotlin.jvm.JvmInline

@JvmInline
value class BookPageImage(val value: Pair<Book, Int>) {
    val book get() = value.first
    val pageIndex get() = value.second
}
