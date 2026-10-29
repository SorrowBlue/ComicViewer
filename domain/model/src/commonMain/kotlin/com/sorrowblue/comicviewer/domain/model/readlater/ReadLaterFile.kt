/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.model.readlater

import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId

/**
 * Represents a file marked as "read later".
 *
 * @property bookshelfId The identifier of the bookshelf containing the file.
 * @property path The full path of the file within the bookshelf.
 * @property modifiedDate The epoch millisecond timestamp when the entry was added or modified.
 */
data class ReadLaterFile(
    val bookshelfId: BookshelfId,
    val path: String,
    val modifiedDate: Long = 0L,
)
