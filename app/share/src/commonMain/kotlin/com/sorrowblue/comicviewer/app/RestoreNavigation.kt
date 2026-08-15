/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.app

import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId

internal data class RestoreNavigation(
    val bookshelfId: BookshelfId,
    val path: String,
    val restorePath: String?,
    val onRestoreComplete: (() -> Unit)? = null,
)
