/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.model.file

import kotlin.jvm.JvmInline

/**
 * Represents a specific page image request within a [Book].
 *
 * @property value A pair of the target [Book] and zero-based page index.
 */
@JvmInline
value class BookPageImage(val value: Pair<Book, Int>) {
    /** The target book containing the page. */
    val book get() = value.first

    /** The zero-based index of the page within the book. */
    val pageIndex get() = value.second
}
