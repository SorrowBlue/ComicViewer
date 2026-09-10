/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.folder.nav

import androidx.navigation3.runtime.NavKey
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId

interface FolderNavKey : NavKey {
    val bookshelfId: BookshelfId
    val path: String
    val restorePath: String?
    val showSearch: Boolean get() = false
    val onRestoreComplete: (() -> Unit)? get() = null
}
