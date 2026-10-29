/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.model.file

/**
 * Lowercase file extension extracted from the string after the last dot (`.`),
 * or an empty string if no dot is present.
 */
val String.extension: String
    get() = substringAfterLast('.', "").lowercase()
