/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.data.storage.client.impl

import com.sorrowblue.comicviewer.data.storage.client.FileClientFactory
import com.sorrowblue.comicviewer.data.storage.client.getFileClient
import com.sorrowblue.comicviewer.domain.model.bookshelf.Bookshelf
import com.sorrowblue.comicviewer.domain.model.file.Book
import com.sorrowblue.comicviewer.domain.repository.file.BookFileReader
import com.sorrowblue.comicviewer.domain.repository.file.BookFileReaderManager
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import logcat.logcat

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
@Inject
internal class BookFileReaderManagerImpl(private val fileClientFactory: FileClientFactory) :
    BookFileReaderManager {

    private var fileReader: BookFileReader? = null
    private var book: Book? = null
    private val mutex = Mutex()

    override suspend fun get(bookshelf: Bookshelf, book: Book): BookFileReader = mutex.withLock {
        val currentReader = fileReader
        if (currentReader != null && this.book == book) {
            logcat { "同じFileReaderを使う。${book.bookshelfId} ${book.path}" }
            currentReader
        } else {
            currentReader?.close()
            val newReader = fileClientFactory.getFileClient(bookshelf).fileReader(book)
            fileReader = newReader
            this.book = book
            newReader
        }
    }

    override suspend fun close(book: Book) {
        mutex.withLock {
            if (this.book == book) {
                logcat { "FileReaderをクローズする。${book.bookshelfId} ${book.path}" }
                fileReader?.close()
                fileReader = null
                this.book = null
            }
        }
    }
}
