package com.sorrowblue.comicviewer.data.infrastructure.mapper

import com.sorrowblue.comicviewer.data.model.bookshelf.BookshelfModelId
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId

fun BookshelfModelId.toBookshelfId() = BookshelfId(value)

fun BookshelfModelId.Companion.from(bookshelfId: BookshelfId) = BookshelfModelId(bookshelfId.value)
