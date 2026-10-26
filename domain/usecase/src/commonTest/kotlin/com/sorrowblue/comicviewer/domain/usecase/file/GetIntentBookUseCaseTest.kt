/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.usecase.file

import com.sorrowblue.comicviewer.domain.model.bookshelf.Bookshelf
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.common.InternalDataApi
import com.sorrowblue.comicviewer.domain.model.common.Resource
import com.sorrowblue.comicviewer.domain.model.file.Book
import com.sorrowblue.comicviewer.domain.model.file.BookFile
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.model.file.Folder
import com.sorrowblue.comicviewer.domain.repository.storage.RemoteStorageClient
import com.sorrowblue.comicviewer.domain.usecase.StubRemoteStorageClient
import kotlin.coroutines.cancellation.CancellationException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue
import kotlinx.coroutines.test.runTest

@OptIn(InternalDataApi::class)
class GetIntentBookUseCaseTest {

    @Test
    fun getIntentBook_whenFileIsBookFile_returnsSuccessWithPageCount() = runTest {
        val uri = "content://example/book.cbz"
        val remoteClientFactory = object : RemoteStorageClient.Factory {
            override fun create(bookshelf: Bookshelf): RemoteStorageClient =
                object : StubRemoteStorageClient() {
                    override suspend fun file(path: String, resolveImageFolder: Boolean): File =
                        BookFile(
                            bookshelfId = BookshelfId(0),
                            name = "book.cbz",
                            parent = "",
                            path = path,
                            size = 1024L,
                            lastModifier = 100L,
                            isHidden = false,
                        )

                    override suspend fun pageCount(book: Book): Int = 48
                }
        }

        val useCase = GetIntentBookUseCaseImpl(remoteClientFactory)
        val result = useCase(uri)

        assertTrue(result is Resource.Success)
        assertEquals(48, result.data.totalPageCount)
    }

    @Test
    fun getIntentBook_whenFileIsFolder_returnsErrorInsteadOfClassCastException() = runTest {
        val uri = "content://example/folder"
        val remoteClientFactory = object : RemoteStorageClient.Factory {
            override fun create(bookshelf: Bookshelf): RemoteStorageClient =
                object : StubRemoteStorageClient() {
                    override suspend fun file(path: String, resolveImageFolder: Boolean): File =
                        Folder(
                            bookshelfId = BookshelfId(0),
                            name = "folder",
                            parent = "",
                            path = path,
                            size = 0L,
                            lastModifier = 100L,
                            isHidden = false,
                        )
                }
        }

        val useCase = GetIntentBookUseCaseImpl(remoteClientFactory)
        val result = useCase(uri)

        assertTrue(result is Resource.Error)
    }

    @Test
    fun getIntentBook_whenCancelled_rethrowsCancellationException() = runTest {
        val uri = "content://example/book.cbz"
        val remoteClientFactory = object : RemoteStorageClient.Factory {
            override fun create(bookshelf: Bookshelf): RemoteStorageClient =
                object : StubRemoteStorageClient() {
                    override suspend fun file(path: String, resolveImageFolder: Boolean): File =
                        throw CancellationException("Cancelled")
                }
        }

        val useCase = GetIntentBookUseCaseImpl(remoteClientFactory)

        assertFailsWith<CancellationException> {
            useCase(uri)
        }
    }
}
