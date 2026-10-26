/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.repository.file

import com.sorrowblue.comicviewer.domain.model.bookshelf.Bookshelf
import com.sorrowblue.comicviewer.domain.model.file.Book

/**
 * Manager port that caches and manages the lifecycle of [BookFileReader] instances for open books.
 */
interface BookFileReaderManager {

    /**
     * Retrieves a [BookFileReader] for the specified book, returning a cached reader if the book is already open.
     *
     * @param bookshelf Bookshelf to which the book belongs.
     * @param book Target book.
     * @return [BookFileReader] instance.
     */
    suspend fun get(bookshelf: Bookshelf, book: Book): BookFileReader

    /**
     * Releases resources held for the specified open book.
     *
     * @param book Book to close.
     */
    suspend fun close(book: Book)
}
