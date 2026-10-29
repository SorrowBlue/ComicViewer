/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.model.file

/**
 * Represents a folder-like [File] entry that contains child items.
 */
sealed interface IFolder : File {
    /** The number of child entries inside the folder. */
    val count: Int
}
