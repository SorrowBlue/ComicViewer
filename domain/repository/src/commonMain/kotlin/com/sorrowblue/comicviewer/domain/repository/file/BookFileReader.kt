/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.repository.file

import kotlinx.io.Sink
import kotlinx.io.Source

/**
 * Reader port that accesses the contents (page streams, sizes, and metadata) of a book file.
 */
interface BookFileReader : AutoCloseable {

    /**
     * Returns the total number of pages in the book.
     *
     * @return Total page count.
     */
    suspend fun pageCount(): Int

    /**
     * Opens a data [Source] for the specified page.
     *
     * @param pageIndex Zero-based page index.
     * @return Data [Source] for the page.
     */
    suspend fun source(pageIndex: Int): Source

    /**
     * Extracts the raw data of the specified page into [sink].
     *
     * @param pageIndex Zero-based page index.
     * @param sink Destination [Sink].
     */
    suspend fun extractTo(pageIndex: Int, sink: Sink)

    /**
     * Returns the byte size of the specified page entry.
     *
     * @param pageIndex Zero-based page index.
     * @return File size in bytes.
     */
    suspend fun fileSize(pageIndex: Int): Long

    /**
     * Returns the entry file name of the specified page.
     *
     * @param pageIndex Zero-based page index.
     * @return Entry file name.
     */
    suspend fun fileName(pageIndex: Int): String
}
