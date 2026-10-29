/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.repository.storage

import com.sorrowblue.comicviewer.domain.model.bookshelf.Bookshelf
import com.sorrowblue.comicviewer.domain.model.file.Book
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.model.file.FileAttribute
import com.sorrowblue.comicviewer.domain.model.storage.RemoteException
import com.sorrowblue.comicviewer.domain.repository.file.BookFileReader
import kotlin.coroutines.cancellation.CancellationException

/**
 * Client port for interacting with local or remote storage backends (e.g., device storage, SMB).
 */
interface RemoteStorageClient {
    /**
     * Factory for creating a [RemoteStorageClient] bound to a specific [Bookshelf].
     */
    interface Factory {
        /**
         * Creates a [RemoteStorageClient] for the given [bookshelf].
         */
        fun create(bookshelf: Bookshelf): RemoteStorageClient
    }

    /**
     * Verifies connectivity and access to the specified [path].
     */
    @Throws(RemoteException::class, CancellationException::class)
    suspend fun connect(path: String)

    /**
     * Returns `true` if a file or directory exists at [path].
     */
    @Throws(RemoteException::class, CancellationException::class)
    suspend fun exists(path: String): Boolean

    /**
     * Lists child [File] entries inside [file] matching [filter].
     *
     * @param file The directory to list.
     * @param resolveImageFolder Whether folders containing images should be resolved as [Book] folders.
     * @param filter Predicate used to filter entries.
     */
    @Throws(RemoteException::class, CancellationException::class)
    suspend fun listFiles(
        file: File,
        resolveImageFolder: Boolean = false,
        filter: (File) -> Boolean,
    ): List<File>

    /**
     * Retrieves metadata for a single [File] at [path].
     *
     * @param path Target file path.
     * @param resolveImageFolder Whether a folder containing images should be resolved as a [Book] folder.
     */
    @Throws(RemoteException::class, CancellationException::class)
    suspend fun file(path: String, resolveImageFolder: Boolean = false): File

    /**
     * Returns the total page count of [book].
     */
    @Throws(RemoteException::class, CancellationException::class)
    suspend fun pageCount(book: Book): Int

    /**
     * Opens a [BookFileReader] for reading pages from [book].
     */
    @Throws(RemoteException::class, CancellationException::class)
    suspend fun fileReader(book: Book): BookFileReader

    /**
     * Retrieves platform or protocol-specific [FileAttribute] flags for [path], if supported.
     */
    @Throws(RemoteException::class, CancellationException::class)
    suspend fun getAttribute(path: String): FileAttribute?

    /**
     * Returns the total byte size of the file or directory at [path].
     */
    @Throws(RemoteException::class, CancellationException::class)
    suspend fun getFileSize(path: String): Long
}
