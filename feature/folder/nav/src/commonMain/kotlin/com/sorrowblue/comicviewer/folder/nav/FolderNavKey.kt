/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.folder.nav

import androidx.navigation3.runtime.NavKey
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class FolderNavKey(
    val bookshelfId: BookshelfId,
    val path: String,
    val restorePath: String? = null,
    val showSearch: Boolean = false,
    @Transient val onRestoreComplete: (() -> Unit)? = null,
) : NavKey
