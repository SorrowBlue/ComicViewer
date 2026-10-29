/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.model.bookshelf

/**
 * Represents the storage type of a [Bookshelf].
 */
enum class BookshelfType {
    /** Network storage accessed via SMB. */
    SMB,

    /** Local device storage. */
    DEVICE,
}
