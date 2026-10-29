/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.model.file

/**
 * Represents a readable comic book entity (either an archive file or an image folder).
 */
sealed interface Book : File {
    /** The zero-based index of the last read page. */
    val lastPageRead: Int

    /** The total number of pages in the book. */
    val totalPageCount: Int

    /** The epoch millisecond timestamp when the book was last read. */
    val lastReadTime: Long
}
