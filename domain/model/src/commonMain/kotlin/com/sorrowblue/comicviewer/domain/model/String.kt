/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.model

val String.extension get() = substringAfterLast('.', "").lowercase()
