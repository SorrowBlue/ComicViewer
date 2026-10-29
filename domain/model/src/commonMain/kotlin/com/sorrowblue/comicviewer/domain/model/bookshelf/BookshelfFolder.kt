/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.model.bookshelf

import com.sorrowblue.comicviewer.domain.model.file.Folder

/**
 * Represents a pair of a [Bookshelf] and its associated [Folder].
 *
 * @property bookshelf The bookshelf entity.
 * @property folder The folder belonging to the bookshelf.
 */
data class BookshelfFolder(val bookshelf: Bookshelf, val folder: Folder)
