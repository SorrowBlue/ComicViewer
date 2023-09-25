package com.sorrowblue.comicviewer.data.mapper

import com.sorrowblue.comicviewer.data.common.bookshelf.BookshelfModelId
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId

fun BookshelfModelId.toBookshelfId() = BookshelfId(value)

fun BookshelfModelId.Companion.from(bookshelfId: BookshelfId) = BookshelfModelId(bookshelfId.value)
