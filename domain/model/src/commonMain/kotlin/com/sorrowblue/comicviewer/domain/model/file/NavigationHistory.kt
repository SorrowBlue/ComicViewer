/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.model.file

/**
 * Represents the navigation history consisting of an ordered folder hierarchy and the last read [Book].
 *
 * @property folderList Ordered list of ancestor folders from root to the book's parent folder.
 * @property book The last read book.
 */
data class NavigationHistory(val folderList: List<Folder>, val book: Book)
