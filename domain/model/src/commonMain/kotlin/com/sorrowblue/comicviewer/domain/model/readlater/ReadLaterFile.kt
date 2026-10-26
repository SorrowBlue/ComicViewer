/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.model.readlater

import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId

data class ReadLaterFile(
    val bookshelfId: BookshelfId,
    val path: String,
    val modifiedDate: Long = 0L,
)
