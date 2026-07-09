/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.service

import com.sorrowblue.comicviewer.domain.model.file.Book

interface BookFileReaderManager {
    suspend fun close(book: Book)
}
