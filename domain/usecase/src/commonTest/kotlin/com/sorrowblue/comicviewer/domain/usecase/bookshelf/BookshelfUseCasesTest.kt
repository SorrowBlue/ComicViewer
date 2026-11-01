/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.usecase.bookshelf

import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.sorrowblue.comicviewer.domain.model.bookshelf.Bookshelf
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfFolder
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.bookshelf.DeviceStorage
import com.sorrowblue.comicviewer.domain.model.common.InternalDataApi
import com.sorrowblue.comicviewer.domain.model.common.Resource
import com.sorrowblue.comicviewer.domain.repository.bookshelf.BookshelfScanManager
import com.sorrowblue.comicviewer.domain.usecase.StubBookshelfRepository
import com.sorrowblue.comicviewer.domain.usecase.invoke
import kotlin.coroutines.cancellation.CancellationException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest

@OptIn(InternalDataApi::class)
class BookshelfUseCasesTest {

    private val bookshelfId = BookshelfId(1)
    private val bookshelf = DeviceStorage(bookshelfId, "Shelf 1", 5, false)

    @Test
    fun flowBookshelfList_isCold_doesNotInvokeRepositoryBeforeCollection() = runTest {
        var repositoryCallCount = 0
        val useCase = FlowBookshelfListUseCaseImpl(
            bookshelfRepository = object : StubBookshelfRepository() {
                override fun allBookshelf(): Resource<Flow<List<Bookshelf>>, Resource.SystemError> {
                    repositoryCallCount++
                    return Resource.Success(flowOf(listOf(bookshelf)))
                }
            },
        )

        val flow = useCase()
        assertEquals(0, repositoryCallCount)

        assertEquals(Resource.Success(listOf<Bookshelf>(bookshelf)), flow.first())
        assertEquals(1, repositoryCallCount)
    }

    @Test
    fun flowBookshelfList_whenSuccess_emitsBookshelfList() = runTest {
        val useCase = FlowBookshelfListUseCaseImpl(
            bookshelfRepository = object : StubBookshelfRepository() {
                override fun allBookshelf(): Resource<Flow<List<Bookshelf>>, Resource.SystemError> =
                    Resource.Success(flowOf(listOf(bookshelf)))
            },
        )

        val result = useCase().first()

        assertEquals(Resource.Success(listOf<Bookshelf>(bookshelf)), result)
    }

    @Test
    fun flowBookshelfList_whenRepositoryReturnsError_emitsSystemError() = runTest {
        val useCase = FlowBookshelfListUseCaseImpl(
            bookshelfRepository = object : StubBookshelfRepository() {
                override fun allBookshelf(): Resource<Flow<List<Bookshelf>>, Resource.SystemError> =
                    Resource.Error(Resource.SystemError(RuntimeException("DB error")))
            },
        )

        val result = useCase().first()

        assertEquals(Resource.Error(FlowBookshelfListUseCase.Error.System), result)
    }

    @Test
    fun flowBookshelfList_whenRepositoryReturnsCancellationError_rethrowsCancellationException() =
        runTest {
            val useCase = FlowBookshelfListUseCaseImpl(
                bookshelfRepository = object : StubBookshelfRepository() {
                    override fun allBookshelf(): Resource<Flow<List<Bookshelf>>, Resource.SystemError> =
                        Resource.Error(Resource.SystemError(CancellationException("Cancelled")))
                },
            )

            assertFailsWith<CancellationException> {
                useCase().first()
            }
        }

    @Test
    fun flowBookshelfList_whenFlowThrowsException_emitsSystemError() = runTest {
        val useCase = FlowBookshelfListUseCaseImpl(
            bookshelfRepository = object : StubBookshelfRepository() {
                override fun allBookshelf(): Resource<Flow<List<Bookshelf>>, Resource.SystemError> =
                    Resource.Success(
                        flow {
                            throw IllegalStateException("Stream failure")
                        },
                    )
            },
        )

        val result = useCase().first()

        assertEquals(Resource.Error(FlowBookshelfListUseCase.Error.System), result)
    }

    @Test
    fun flowBookshelfList_whenCancelled_rethrowsCancellationException() = runTest {
        val useCase = FlowBookshelfListUseCaseImpl(
            bookshelfRepository = object : StubBookshelfRepository() {
                override fun allBookshelf(): Resource<Flow<List<Bookshelf>>, Resource.SystemError> =
                    Resource.Success(
                        flow {
                            throw CancellationException("Cancelled")
                        },
                    )
            },
        )

        assertFailsWith<CancellationException> {
            useCase().first()
        }
    }

    @Test
    fun updateDeletionFlag_whenSuccess_updatesDeletedAndReturnsSuccess() = runTest {
        var updatedId: BookshelfId? = null
        var updatedFlag: Boolean? = null

        val useCase = UpdateDeletionFlagUseCaseImpl(
            bookshelfRepository = object : StubBookshelfRepository() {
                override suspend fun updateDeleted(bookshelfId: BookshelfId, isDeleted: Boolean) {
                    updatedId = bookshelfId
                    updatedFlag = isDeleted
                }
            },
        )

        val result = useCase(UpdateDeletionFlagUseCase.Request(bookshelfId, true))

        assertEquals(Resource.Success(Unit), result)
        assertEquals(bookshelfId, updatedId)
        assertEquals(true, updatedFlag)
    }

    @Test
    fun updateDeletionFlag_whenRepositoryThrowsException_returnsError() = runTest {
        val useCase = UpdateDeletionFlagUseCaseImpl(
            bookshelfRepository = object : StubBookshelfRepository() {
                override suspend fun updateDeleted(
                    bookshelfId: BookshelfId,
                    isDeleted: Boolean,
                ): Unit = error("Update failed")
            },
        )

        val result = useCase(UpdateDeletionFlagUseCase.Request(bookshelfId, true))

        assertEquals(Resource.Error(Unit), result)
    }

    @Test
    fun updateDeletionFlag_whenCancelled_rethrowsCancellationException() = runTest {
        val useCase = UpdateDeletionFlagUseCaseImpl(
            bookshelfRepository = object : StubBookshelfRepository() {
                override suspend fun updateDeleted(
                    bookshelfId: BookshelfId,
                    isDeleted: Boolean,
                ): Unit = throw CancellationException("Cancelled")
            },
        )

        assertFailsWith<CancellationException> {
            useCase(UpdateDeletionFlagUseCase.Request(bookshelfId, false))
        }
    }

    @Test
    fun manageBookshelfScan_delegatesAllOperationsToScanManager() = runTest {
        val scannedFileIds = mutableListOf<BookshelfId>()
        val scannedThumbnailIds = mutableListOf<BookshelfId>()

        val scanManager = object : BookshelfScanManager {
            override fun isScanningFile(bookshelfId: BookshelfId): Flow<Boolean> = flowOf(true)
            override fun isScanningThumbnail(bookshelfId: BookshelfId): Flow<Boolean> =
                flowOf(false)

            override fun scanFile(bookshelfId: BookshelfId) {
                scannedFileIds.add(bookshelfId)
            }

            override fun scanThumbnail(bookshelfId: BookshelfId) {
                scannedThumbnailIds.add(bookshelfId)
            }
        }

        val useCase = ManageBookshelfScanUseCaseImpl(scanManager)

        assertEquals(true, useCase.isScanningFile(bookshelfId).first())
        assertEquals(false, useCase.isScanningThumbnail(bookshelfId).first())

        useCase.scanFile(bookshelfId)
        useCase.scanThumbnail(bookshelfId)

        assertEquals(listOf(bookshelfId), scannedFileIds)
        assertEquals(listOf(bookshelfId), scannedThumbnailIds)
    }

    @Test
    fun pagingBookshelfFolder_delegatesToBookshelfRepository() = runTest {
        val expectedPagingData = PagingData.empty<BookshelfFolder>()
        var requestedConfig: PagingConfig? = null

        val useCase = PagingBookshelfFolderUseCaseImpl(
            bookshelfRepository = object : StubBookshelfRepository() {
                override fun pagingSource(
                    pagingConfig: PagingConfig,
                ): Flow<PagingData<BookshelfFolder>> {
                    requestedConfig = pagingConfig
                    return flowOf(expectedPagingData)
                }
            },
        )

        val pagingConfig = PagingConfig(pageSize = 20)
        val items = useCase(PagingBookshelfFolderUseCase.Request(pagingConfig)).toList()

        assertEquals(pagingConfig, requestedConfig)
        assertEquals(listOf(expectedPagingData), items)
    }
}
