/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.usecase.file

import com.sorrowblue.comicviewer.domain.model.bookshelf.Bookshelf
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.bookshelf.DeviceStorage
import com.sorrowblue.comicviewer.domain.model.common.InternalDataApi
import com.sorrowblue.comicviewer.domain.model.common.Resource
import com.sorrowblue.comicviewer.domain.model.file.FileAttribute
import com.sorrowblue.comicviewer.domain.model.storage.RemoteException
import com.sorrowblue.comicviewer.domain.repository.storage.RemoteStorageClient
import com.sorrowblue.comicviewer.domain.usecase.StubBookshelfRepository
import com.sorrowblue.comicviewer.domain.usecase.StubRemoteStorageClient
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
class GetFileAttributeAndSizeUseCaseTest {

    private val bookshelfId = BookshelfId(1)
    private val bookshelf = DeviceStorage(bookshelfId, "Shelf", 0, false)
    private val path = "/manga/book.cbz"

    @Test
    fun getFileAttribute_whenSuccess_returnsAttribute() = runTest {
        val attribute = FileAttribute(archive = true, readonly = true)
        val useCase = GetFileAttributeUseCaseImpl(
            bookshelfRepository = object : StubBookshelfRepository() {
                override fun flow(bookshelfId: BookshelfId): Flow<Bookshelf?> = flowOf(bookshelf)
            },
            remoteStorageClientFactory = object : RemoteStorageClient.Factory {
                override fun create(bookshelf: Bookshelf): RemoteStorageClient =
                    object : StubRemoteStorageClient() {
                        override suspend fun getAttribute(path: String): FileAttribute = attribute
                    }
            },
        )

        val result = useCase(GetFileAttributeUseCase.Request(bookshelfId, path)).first()
        assertEquals(Resource.Success(attribute), result)
    }

    @Test
    fun getFileAttribute_whenRemoteThrowsNotFound_returnsNotFoundError() = runTest {
        val useCase = GetFileAttributeUseCaseImpl(
            bookshelfRepository = object : StubBookshelfRepository() {
                override fun flow(bookshelfId: BookshelfId): Flow<Bookshelf?> = flowOf(bookshelf)
            },
            remoteStorageClientFactory = object : RemoteStorageClient.Factory {
                override fun create(bookshelf: Bookshelf): RemoteStorageClient =
                    object : StubRemoteStorageClient() {
                        override suspend fun getAttribute(path: String): FileAttribute =
                            throw RemoteException.NotFound()
                    }
            },
        )

        val result = useCase(GetFileAttributeUseCase.Request(bookshelfId, path)).first()
        assertEquals(Resource.Error(GetFileAttributeUseCase.Error.NotFound), result)
    }

    @Test
    fun getFileAttribute_whenUpstreamFlowThrows_returnsSystemError() = runTest {
        val useCase = GetFileAttributeUseCaseImpl(
            bookshelfRepository = object : StubBookshelfRepository() {
                override fun flow(bookshelfId: BookshelfId): Flow<Bookshelf?> = flow {
                    throw IllegalStateException("Upstream failure")
                }
            },
            remoteStorageClientFactory = object : RemoteStorageClient.Factory {
                override fun create(bookshelf: Bookshelf): RemoteStorageClient =
                    StubRemoteStorageClient()
            },
        )

        val result = useCase(GetFileAttributeUseCase.Request(bookshelfId, path)).first()
        assertEquals(Resource.Error(GetFileAttributeUseCase.Error.System), result)
    }

    @Test
    fun getFileAttribute_whenCancelled_rethrowsCancellationException() = runTest {
        val useCase = GetFileAttributeUseCaseImpl(
            bookshelfRepository = object : StubBookshelfRepository() {
                override fun flow(bookshelfId: BookshelfId): Flow<Bookshelf?> = flowOf(bookshelf)
            },
            remoteStorageClientFactory = object : RemoteStorageClient.Factory {
                override fun create(bookshelf: Bookshelf): RemoteStorageClient =
                    object : StubRemoteStorageClient() {
                        override suspend fun getAttribute(path: String): FileAttribute =
                            throw CancellationException("Cancelled")
                    }
            },
        )

        assertFailsWith<CancellationException> {
            useCase(GetFileAttributeUseCase.Request(bookshelfId, path)).first()
        }
    }

    @Test
    fun getFileSize_whenSuccess_returnsSize() = runTest {
        val useCase = GetFileSizeUseCaseImpl(
            bookshelfRepository = object : StubBookshelfRepository() {
                override fun flow(bookshelfId: BookshelfId): Flow<Bookshelf?> = flowOf(bookshelf)
            },
            remoteStorageClientFactory = object : RemoteStorageClient.Factory {
                override fun create(bookshelf: Bookshelf): RemoteStorageClient =
                    object : StubRemoteStorageClient() {
                        override suspend fun getFileSize(path: String): Long = 4096L
                    }
            },
        )

        val result = useCase(GetFileSizeUseCase.Request(bookshelfId, path)).first()
        assertEquals(Resource.Success(4096L), result)
    }

    @Test
    fun getFileSize_whenRemoteThrowsNotFound_returnsNotFoundError() = runTest {
        val useCase = GetFileSizeUseCaseImpl(
            bookshelfRepository = object : StubBookshelfRepository() {
                override fun flow(bookshelfId: BookshelfId): Flow<Bookshelf?> = flowOf(bookshelf)
            },
            remoteStorageClientFactory = object : RemoteStorageClient.Factory {
                override fun create(bookshelf: Bookshelf): RemoteStorageClient =
                    object : StubRemoteStorageClient() {
                        override suspend fun getFileSize(path: String): Long =
                            throw RemoteException.NotFound()
                    }
            },
        )

        val result = useCase(GetFileSizeUseCase.Request(bookshelfId, path)).first()
        assertEquals(Resource.Error(GetFileSizeUseCase.Error.NotFound), result)
    }

    @Test
    fun getFileSize_whenUpstreamFlowThrows_returnsSystemError() = runTest {
        val useCase = GetFileSizeUseCaseImpl(
            bookshelfRepository = object : StubBookshelfRepository() {
                override fun flow(bookshelfId: BookshelfId): Flow<Bookshelf?> = flow {
                    throw IllegalStateException("Upstream failure")
                }
            },
            remoteStorageClientFactory = object : RemoteStorageClient.Factory {
                override fun create(bookshelf: Bookshelf): RemoteStorageClient =
                    StubRemoteStorageClient()
            },
        )

        val result = useCase(GetFileSizeUseCase.Request(bookshelfId, path)).first()
        assertEquals(Resource.Error(GetFileSizeUseCase.Error.System), result)
    }

    @Test
    fun getFileSize_whenCancelled_rethrowsCancellationException() = runTest {
        val useCase = GetFileSizeUseCaseImpl(
            bookshelfRepository = object : StubBookshelfRepository() {
                override fun flow(bookshelfId: BookshelfId): Flow<Bookshelf?> = flowOf(bookshelf)
            },
            remoteStorageClientFactory = object : RemoteStorageClient.Factory {
                override fun create(bookshelf: Bookshelf): RemoteStorageClient =
                    object : StubRemoteStorageClient() {
                        override suspend fun getFileSize(path: String): Long =
                            throw CancellationException("Cancelled")
                    }
            },
        )

        assertFailsWith<CancellationException> {
            useCase(GetFileSizeUseCase.Request(bookshelfId, path)).first()
        }
    }
}
