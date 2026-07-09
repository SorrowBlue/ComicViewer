/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.model.bookshelf

data object ShareContents : Bookshelf {
    override val id: BookshelfId = BookshelfId()

    override val displayName: String = "ShareContents"

    override val fileCount = 0

    override val isDeleted = false

    override val type: BookshelfType? = null
}
