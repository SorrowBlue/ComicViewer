/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.model.file

sealed interface IFolder : File {
    val count: Int
}
