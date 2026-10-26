/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.usecase.readlater

import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.common.InternalDataApi
import com.sorrowblue.comicviewer.domain.model.common.Resource
import com.sorrowblue.comicviewer.domain.model.readlater.ReadLaterFile
import com.sorrowblue.comicviewer.domain.usecase.StubReadLaterFileRepository
import com.sorrowblue.comicviewer.domain.usecase.StubSendFatalErrorUseCase
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest

@OptIn(InternalDataApi::class)
class ReadLaterUseCaseTest {

    private val bookshelfId = BookshelfId(1)
    private val path = "/folder/book.cbz"
    private val persistedReadLaterFile = ReadLaterFile(bookshelfId, path, modifiedDate = 123_456L)

    @Test
    fun addReadLaterUseCase_returnsPersistedEntityFromRepository() = runTest {
        val fatalErrorUseCase = StubSendFatalErrorUseCase()
        val useCase = AddReadLaterUseCaseImpl(
            readLaterFileRepository = object : StubReadLaterFileRepository() {
                override suspend fun updateOrAdd(
                    file: ReadLaterFile,
                ): Resource<ReadLaterFile, Resource.SystemError> {
                    assertEquals(bookshelfId, file.bookshelfId)
                    assertEquals(path, file.path)
                    return Resource.Success(persistedReadLaterFile)
                }
            },
            sendFatalErrorUseCase = fatalErrorUseCase,
        )

        val req1 = AddReadLaterUseCase.Request(bookshelfId, path)
        val req2 = AddReadLaterUseCase.Request(bookshelfId, path)
        assertEquals(req1, req2)

        val result = useCase(req1)
        assertEquals(Resource.Success(persistedReadLaterFile), result)
        assertEquals(0, fatalErrorUseCase.reportedThrowables.size)
    }

    @Test
    fun deleteReadLaterUseCase_deletesMatchingEntityAndHasDeterministicRequestEquality() = runTest {
        val fatalErrorUseCase = StubSendFatalErrorUseCase()
        val deletedFiles = mutableListOf<ReadLaterFile>()
        val useCase = DeleteReadLaterUseCaseImpl(
            readLaterFileRepository = object : StubReadLaterFileRepository() {
                override suspend fun delete(
                    file: ReadLaterFile,
                ): Resource<Unit, Resource.SystemError> {
                    deletedFiles.add(file)
                    return Resource.Success(Unit)
                }
            },
            sendFatalErrorUseCase = fatalErrorUseCase,
        )

        val req1 = DeleteReadLaterUseCase.Request(bookshelfId, path)
        val req2 = DeleteReadLaterUseCase.Request(bookshelfId, path)
        assertEquals(req1, req2)

        val result = useCase(req1)
        assertEquals(Resource.Success(Unit), result)
        assertEquals(1, deletedFiles.size)
        assertEquals(bookshelfId, deletedFiles[0].bookshelfId)
        assertEquals(path, deletedFiles[0].path)
    }

    @Test
    fun existsReadLaterUseCase_emitsExistenceStatus() = runTest {
        val fatalErrorUseCase = StubSendFatalErrorUseCase()
        val useCase = ExistsReadLaterUseCaseImpl(
            readLaterFileRepository = object : StubReadLaterFileRepository() {
                override fun exists(
                    file: ReadLaterFile,
                ): Resource<Flow<Boolean>, Resource.SystemError> = Resource.Success(flowOf(true))
            },
            sendFatalErrorUseCase = fatalErrorUseCase,
        )

        val result = useCase(ExistsReadLaterUseCase.Request(bookshelfId, path)).first()
        assertEquals(Resource.Success(true), result)
    }

    @Test
    fun existsReadLaterUseCase_whenUpstreamFlowThrows_reportsFatalErrorAndEmitsError() = runTest {
        val fatalErrorUseCase = StubSendFatalErrorUseCase()
        val expectedException = IllegalStateException("Upstream flow failure")
        val useCase = ExistsReadLaterUseCaseImpl(
            readLaterFileRepository = object : StubReadLaterFileRepository() {
                override fun exists(
                    file: ReadLaterFile,
                ): Resource<Flow<Boolean>, Resource.SystemError> = Resource.Success(
                    flow { throw expectedException },
                )
            },
            sendFatalErrorUseCase = fatalErrorUseCase,
        )

        val result = useCase(ExistsReadLaterUseCase.Request(bookshelfId, path)).first()
        assertEquals(Resource.Error(Unit), result)
        assertEquals(listOf<Throwable>(expectedException), fatalErrorUseCase.reportedThrowables)
    }

    @Test
    fun deleteAllReadLaterUseCase_whenSuccess_returnsSuccess() = runTest {
        val fatalErrorUseCase = StubSendFatalErrorUseCase()
        var deleteAllCalled = false
        val useCase = DeleteAllReadLaterUseCaseImpl(
            readLaterFileRepository = object : StubReadLaterFileRepository() {
                override suspend fun deleteAll(): Resource<Unit, Resource.SystemError> {
                    deleteAllCalled = true
                    return Resource.Success(Unit)
                }
            },
            sendFatalErrorUseCase = fatalErrorUseCase,
        )

        val result = useCase(Unit)
        assertEquals(Resource.Success(Unit), result)
        assertEquals(true, deleteAllCalled)
        assertEquals(0, fatalErrorUseCase.reportedThrowables.size)
    }

    @Test
    fun deleteAllReadLaterUseCase_whenError_reportsFatalErrorAndReturnsError() = runTest {
        val fatalErrorUseCase = StubSendFatalErrorUseCase()
        val expectedException = IllegalStateException("DeleteAll failed")
        val useCase = DeleteAllReadLaterUseCaseImpl(
            readLaterFileRepository = object : StubReadLaterFileRepository() {
                override suspend fun deleteAll(): Resource<Unit, Resource.SystemError> =
                    Resource.Error(Resource.SystemError(expectedException))
            },
            sendFatalErrorUseCase = fatalErrorUseCase,
        )

        val result = useCase(Unit)
        assertEquals(Resource.Error(Unit), result)
        assertEquals(listOf<Throwable>(expectedException), fatalErrorUseCase.reportedThrowables)
    }

    @Test
    fun addReadLaterUseCase_whenRepositoryThrowsUnexpectedException_reportsFatalErrorAndReturnsError() =
        runTest {
            val fatalErrorUseCase = StubSendFatalErrorUseCase()
            val expectedException = IllegalStateException("Unexpected exception")
            val useCase = AddReadLaterUseCaseImpl(
                readLaterFileRepository = object : StubReadLaterFileRepository() {
                    override suspend fun updateOrAdd(
                        file: ReadLaterFile,
                    ): Resource<ReadLaterFile, Resource.SystemError> = throw expectedException
                },
                sendFatalErrorUseCase = fatalErrorUseCase,
            )

            val result = useCase(AddReadLaterUseCase.Request(bookshelfId, path))
            assertEquals(Resource.Error(Unit), result)
            assertEquals(listOf<Throwable>(expectedException), fatalErrorUseCase.reportedThrowables)
        }
}
