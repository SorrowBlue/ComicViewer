/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.usecase.bookshelf

import com.sorrowblue.comicviewer.domain.model.bookshelf.Bookshelf
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfFolder
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.bookshelf.DeviceStorage
import com.sorrowblue.comicviewer.domain.model.common.InternalDataApi
import com.sorrowblue.comicviewer.domain.model.common.Resource
import com.sorrowblue.comicviewer.domain.model.file.Folder
import com.sorrowblue.comicviewer.domain.usecase.StubBookshelfRepository
import com.sorrowblue.comicviewer.domain.usecase.StubFileRepository
import com.sorrowblue.comicviewer.domain.usecase.StubSendFatalErrorUseCase
import kotlin.coroutines.cancellation.CancellationException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest

@OptIn(InternalDataApi::class)
class GetBookshelfInfoUseCaseTest {

    private val bookshelfId = BookshelfId(1)
    private val bookshelf = DeviceStorage(bookshelfId, "Shelf", 0, false)
    private val rootFolder = Folder(bookshelfId, "root", "", "/root", 0, 0, false)

    @Test
    fun getBookshelfInfo_whenBookshelfAndFolderExist_returnsSuccess() = runTest {
        val fatalErrorUseCase = StubSendFatalErrorUseCase()
        val useCase = GetBookshelfInfoUseCaseImpl(
            bookshelfRepository = object : StubBookshelfRepository() {
                override fun flow(bookshelfId: BookshelfId): Flow<Bookshelf?> = flowOf(bookshelf)
            },
            fileRepository = object : StubFileRepository() {
                override suspend fun root(id: BookshelfId): Folder = rootFolder
            },
            sendFatalErrorUseCase = fatalErrorUseCase,
        )

        val result = useCase(bookshelfId).first()
        assertEquals(Resource.Success(BookshelfFolder(bookshelf, rootFolder)), result)
        assertEquals(0, fatalErrorUseCase.reportedThrowables.size)
    }

    @Test
    fun getBookshelfInfo_whenBookshelfNotFound_returnsNotFoundWithoutReportingFatalError() =
        runTest {
            val fatalErrorUseCase = StubSendFatalErrorUseCase()
            val useCase = GetBookshelfInfoUseCaseImpl(
                bookshelfRepository = object : StubBookshelfRepository() {
                    override fun flow(bookshelfId: BookshelfId): Flow<Bookshelf?> = flowOf(null)
                },
                fileRepository = object : StubFileRepository() {},
                sendFatalErrorUseCase = fatalErrorUseCase,
            )

            val result = useCase(bookshelfId).first()
            assertEquals(Resource.Error(GetBookshelfInfoUseCase.Error.NotFound), result)
            assertEquals(0, fatalErrorUseCase.reportedThrowables.size)
        }

    @Test
    fun getBookshelfInfo_whenRootFolderNotFound_returnsNotFoundWithoutReportingFatalError() =
        runTest {
            val fatalErrorUseCase = StubSendFatalErrorUseCase()
            val useCase = GetBookshelfInfoUseCaseImpl(
                bookshelfRepository = object : StubBookshelfRepository() {
                    override fun flow(bookshelfId: BookshelfId): Flow<Bookshelf?> =
                        flowOf(bookshelf)
                },
                fileRepository = object : StubFileRepository() {
                    override suspend fun root(id: BookshelfId): Folder? = null
                },
                sendFatalErrorUseCase = fatalErrorUseCase,
            )

            val result = useCase(bookshelfId).first()
            assertEquals(Resource.Error(GetBookshelfInfoUseCase.Error.NotFound), result)
            assertEquals(0, fatalErrorUseCase.reportedThrowables.size)
        }

    @Test
    fun getBookshelfInfo_whenRootThrowsException_reportsFatalErrorAndReturnsSystemError() =
        runTest {
            val fatalErrorUseCase = StubSendFatalErrorUseCase()
            val useCase = GetBookshelfInfoUseCaseImpl(
                bookshelfRepository = object : StubBookshelfRepository() {
                    override fun flow(bookshelfId: BookshelfId): Flow<Bookshelf?> =
                        flowOf(bookshelf)
                },
                fileRepository = object : StubFileRepository() {
                    override suspend fun root(id: BookshelfId): Folder = error("DB read error")
                },
                sendFatalErrorUseCase = fatalErrorUseCase,
            )

            val result = useCase(bookshelfId).first()
            assertEquals(Resource.Error(GetBookshelfInfoUseCase.Error.System), result)
            assertEquals(1, fatalErrorUseCase.reportedThrowables.size)
        }

    @Test
    fun getBookshelfInfo_whenCancelled_rethrowsCancellationException() = runTest {
        val fatalErrorUseCase = StubSendFatalErrorUseCase()
        val useCase = GetBookshelfInfoUseCaseImpl(
            bookshelfRepository = object : StubBookshelfRepository() {
                override fun flow(bookshelfId: BookshelfId): Flow<Bookshelf?> = flowOf(bookshelf)
            },
            fileRepository = object : StubFileRepository() {
                override suspend fun root(id: BookshelfId): Folder =
                    throw CancellationException("Cancelled")
            },
            sendFatalErrorUseCase = fatalErrorUseCase,
        )

        assertFailsWith<CancellationException> {
            useCase(bookshelfId).first()
        }
        assertEquals(0, fatalErrorUseCase.reportedThrowables.size)
    }

    @Test
    fun getBookshelfInfo_whenUpstreamFlowThrows_reportsFatalErrorAndEmitsSystemError() = runTest {
        val fatalErrorUseCase = StubSendFatalErrorUseCase()
        val useCase = GetBookshelfInfoUseCaseImpl(
            bookshelfRepository = object : StubBookshelfRepository() {
                override fun flow(bookshelfId: BookshelfId): Flow<Bookshelf?> = flow {
                    throw IllegalStateException("Upstream flow failure")
                }
            },
            fileRepository = StubFileRepository(),
            sendFatalErrorUseCase = fatalErrorUseCase,
        )

        val result = useCase(bookshelfId).first()
        assertEquals(Resource.Error(GetBookshelfInfoUseCase.Error.System), result)
        assertEquals(1, fatalErrorUseCase.reportedThrowables.size)
    }
}
