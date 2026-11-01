/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.usecase.file

import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.sorrowblue.comicviewer.domain.model.bookshelf.Bookshelf
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.common.InternalDataApi
import com.sorrowblue.comicviewer.domain.model.common.Resource
import com.sorrowblue.comicviewer.domain.model.file.Book
import com.sorrowblue.comicviewer.domain.model.file.BookFile
import com.sorrowblue.comicviewer.domain.model.file.BookThumbnail
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.model.search.SearchCondition
import com.sorrowblue.comicviewer.domain.repository.file.BookFileReader
import com.sorrowblue.comicviewer.domain.repository.file.BookFileReaderManager
import com.sorrowblue.comicviewer.domain.usecase.StubFileRepository
import com.sorrowblue.comicviewer.domain.usecase.invoke
import kotlin.coroutines.cancellation.CancellationException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue
import kotlin.time.Clock
import kotlin.time.Instant
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest

@OptIn(InternalDataApi::class)
class FileHistoryAndPagingUseCasesTest {

    private val bookshelfId = BookshelfId(1)
    private val path = "/manga/vol1.cbz"
    private val book = BookFile(
        bookshelfId = bookshelfId,
        name = "vol1.cbz",
        parent = "/manga",
        path = path,
        size = 1024L,
        lastModifier = 100L,
        isHidden = false,
    )

    @Test
    fun clearAllHistory_whenSuccess_deletesAllHistoryAndReturnsSuccess() = runTest {
        var deleteAllHistoryCalled = false
        val useCase = ClearAllHistoryUseCaseImpl(
            fileRepository = object : StubFileRepository() {
                override suspend fun deleteAllHistory() {
                    deleteAllHistoryCalled = true
                }
            },
        )

        val result = useCase()

        assertEquals(Resource.Success(Unit), result)
        assertTrue(deleteAllHistoryCalled)
    }

    @Test
    fun clearAllHistory_whenRepositoryThrowsException_returnsError() = runTest {
        val useCase = ClearAllHistoryUseCaseImpl(
            fileRepository = object : StubFileRepository() {
                override suspend fun deleteAllHistory(): Unit = error("DB error")
            },
        )

        val result = useCase()

        assertEquals(Resource.Error(Unit), result)
    }

    @Test
    fun clearAllHistory_whenCancelled_rethrowsCancellationException() = runTest {
        val useCase = ClearAllHistoryUseCaseImpl(
            fileRepository = object : StubFileRepository() {
                override suspend fun deleteAllHistory(): Unit =
                    throw CancellationException("Cancelled")
            },
        )

        assertFailsWith<CancellationException> {
            useCase()
        }
    }

    @Test
    fun closeBook_whenSuccess_closesReaderAndReturnsSuccess() = runTest {
        var closedBook: Book? = null
        val useCase = CloseBookUseCaseImpl(
            bookFileReaderManager = object : BookFileReaderManager {
                override suspend fun get(bookshelf: Bookshelf, book: Book): BookFileReader = TODO()
                override suspend fun close(book: Book) {
                    closedBook = book
                }
            },
        )

        val result = useCase(book)

        assertEquals(Resource.Success(Unit), result)
        assertEquals(book, closedBook)
    }

    @Test
    fun closeBook_whenManagerThrowsException_returnsError() = runTest {
        val useCase = CloseBookUseCaseImpl(
            bookFileReaderManager = object : BookFileReaderManager {
                override suspend fun get(bookshelf: Bookshelf, book: Book): BookFileReader = TODO()
                override suspend fun close(book: Book): Unit = error("Close failure")
            },
        )

        val result = useCase(book)

        assertEquals(Resource.Error(Unit), result)
    }

    @Test
    fun closeBook_whenCancelled_rethrowsCancellationException() = runTest {
        val useCase = CloseBookUseCaseImpl(
            bookFileReaderManager = object : BookFileReaderManager {
                override suspend fun get(bookshelf: Bookshelf, book: Book): BookFileReader = TODO()
                override suspend fun close(book: Book): Unit =
                    throw CancellationException("Cancelled")
            },
        )

        assertFailsWith<CancellationException> {
            useCase(book)
        }
    }

    @Test
    fun updateLastReadPage_whenSuccess_updatesHistoryAndReturnsSuccess() = runTest {
        var capturedPath: String? = null
        var capturedBookshelfId: BookshelfId? = null
        var capturedPage: Int? = null
        var capturedTimestamp: Long? = null
        val fixedClock = object : Clock {
            override fun now(): Instant = Instant.fromEpochMilliseconds(123_456_789L)
        }

        val useCase = UpdateLastReadPageUseCaseImpl(
            fileRepository = object : StubFileRepository() {
                override suspend fun updateHistory(
                    path: String,
                    bookshelfId: BookshelfId,
                    lastReadPage: Int,
                    lastReading: Long,
                ) {
                    capturedPath = path
                    capturedBookshelfId = bookshelfId
                    capturedPage = lastReadPage
                    capturedTimestamp = lastReading
                }
            },
            clock = fixedClock,
        )

        val request1 = UpdateLastReadPageUseCase.Request(
            bookshelfId = bookshelfId,
            path = path,
            lastReadPage = 42,
        )
        val request2 = UpdateLastReadPageUseCase.Request(
            bookshelfId = bookshelfId,
            path = path,
            lastReadPage = 42,
        )
        assertEquals(request1, request2)

        val result = useCase(request1)

        assertEquals(Resource.Success(Unit), result)
        assertEquals(path, capturedPath)
        assertEquals(bookshelfId, capturedBookshelfId)
        assertEquals(42, capturedPage)
        assertEquals(123_456_789L, capturedTimestamp)
    }

    @Test
    fun updateLastReadPage_whenRepositoryThrowsException_returnsError() = runTest {
        val useCase = UpdateLastReadPageUseCaseImpl(
            fileRepository = object : StubFileRepository() {
                override suspend fun updateHistory(
                    path: String,
                    bookshelfId: BookshelfId,
                    lastReadPage: Int,
                    lastReading: Long,
                ): Unit = error("Update history failure")
            },
        )

        val result = useCase(
            UpdateLastReadPageUseCase.Request(
                bookshelfId = bookshelfId,
                path = path,
                lastReadPage = 10,
            ),
        )

        assertEquals(Resource.Error(Unit), result)
    }

    @Test
    fun updateLastReadPage_whenCancelled_rethrowsCancellationException() = runTest {
        val useCase = UpdateLastReadPageUseCaseImpl(
            fileRepository = object : StubFileRepository() {
                override suspend fun updateHistory(
                    path: String,
                    bookshelfId: BookshelfId,
                    lastReadPage: Int,
                    lastReading: Long,
                ): Unit = throw CancellationException("Cancelled")
            },
        )

        assertFailsWith<CancellationException> {
            useCase(
                UpdateLastReadPageUseCase.Request(
                    bookshelfId = bookshelfId,
                    path = path,
                    lastReadPage = 10,
                ),
            )
        }
    }

    @Test
    fun pagingBookshelfBook_delegatesToFileRepository() = runTest {
        val expectedPagingData = PagingData.empty<BookThumbnail>()
        var capturedBookshelfId: BookshelfId? = null
        var capturedConfig: PagingConfig? = null

        val useCase = PagingBookshelfBookUseCaseImpl(
            fileRepository = object : StubFileRepository() {
                override fun pagingSource(
                    bookshelfId: BookshelfId,
                    pagingConfig: PagingConfig,
                ): Flow<PagingData<BookThumbnail>> {
                    capturedBookshelfId = bookshelfId
                    capturedConfig = pagingConfig
                    return flowOf(expectedPagingData)
                }
            },
        )

        val pagingConfig = PagingConfig(pageSize = 20)
        val items = useCase(PagingBookshelfBookUseCase.Request(bookshelfId, pagingConfig)).toList()

        assertEquals(listOf(expectedPagingData), items)
        assertEquals(bookshelfId, capturedBookshelfId)
        assertEquals(pagingConfig, capturedConfig)
    }

    @Test
    fun pagingHistoryBook_delegatesToFileRepository() = runTest {
        val expectedPagingData = PagingData.empty<Book>()
        var capturedConfig: PagingConfig? = null

        val useCase = PagingHistoryBookUseCaseImpl(
            fileRepository = object : StubFileRepository() {
                override fun pagingHistoryBookSource(
                    pagingConfig: PagingConfig,
                ): Flow<PagingData<Book>> {
                    capturedConfig = pagingConfig
                    return flowOf(expectedPagingData)
                }
            },
        )

        val pagingConfig = PagingConfig(pageSize = 20)
        val items = useCase(pagingConfig).toList()

        assertEquals(listOf(expectedPagingData), items)
        assertEquals(pagingConfig, capturedConfig)
    }

    @Test
    fun pagingQueryFile_delegatesToFileRepository() = runTest {
        val expectedPagingData = PagingData.empty<File>()
        val condition = SearchCondition(query = "test")
        var capturedBookshelfId: BookshelfId? = null
        var capturedCondition: SearchCondition? = null
        var capturedConfig: PagingConfig? = null

        val useCase = PagingQueryFileUseCaseImpl(
            fileRepository = object : StubFileRepository() {
                override fun pagingDataFlow(
                    pagingConfig: PagingConfig,
                    bookshelfId: BookshelfId?,
                    searchCondition: () -> SearchCondition,
                ): Flow<PagingData<File>> {
                    capturedConfig = pagingConfig
                    capturedBookshelfId = bookshelfId
                    capturedCondition = searchCondition()
                    return flowOf(expectedPagingData)
                }
            },
        )

        val pagingConfig = PagingConfig(pageSize = 20)
        val items = useCase(
            PagingQueryFileUseCase.Request(
                bookshelfId = bookshelfId,
                searchCondition = { condition },
                pagingConfig = pagingConfig,
            ),
        ).toList()

        assertEquals(listOf(expectedPagingData), items)
        assertEquals(bookshelfId, capturedBookshelfId)
        assertEquals(condition, capturedCondition)
        assertEquals(pagingConfig, capturedConfig)
    }
}
