/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.usecase.file

import com.sorrowblue.comicviewer.domain.model.bookshelf.Bookshelf
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.bookshelf.DeviceStorage
import com.sorrowblue.comicviewer.domain.model.common.InternalDataApi
import com.sorrowblue.comicviewer.domain.model.common.Resource
import com.sorrowblue.comicviewer.domain.model.file.Book
import com.sorrowblue.comicviewer.domain.model.file.BookFile
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.model.settings.FolderSettings
import com.sorrowblue.comicviewer.domain.model.storage.RemoteException
import com.sorrowblue.comicviewer.domain.repository.FileRepositoryQueryError
import com.sorrowblue.comicviewer.domain.repository.storage.RemoteStorageClient
import com.sorrowblue.comicviewer.domain.usecase.StubBookshelfRepository
import com.sorrowblue.comicviewer.domain.usecase.StubFileRepository
import com.sorrowblue.comicviewer.domain.usecase.StubRemoteStorageClient
import com.sorrowblue.comicviewer.domain.usecase.StubSettingsRepository
import kotlin.coroutines.cancellation.CancellationException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest

@OptIn(InternalDataApi::class)
class GetBookUseCaseTest {

    private val defaultSettingsRepository = object : StubSettingsRepository() {
        override val folderSettings: Flow<FolderSettings> = flowOf(FolderSettings())
    }

    @Test
    fun getBook_updatesDatabaseOnceWithTotalPageCount() = runTest {
        val bookshelfId = BookshelfId(1)
        val bookshelf = DeviceStorage(
            id = bookshelfId,
            displayName = "Test",
            fileCount = 0,
            isDeleted = false,
        )
        val path = "/manga/vol1.cbz"

        var updateSimpleCount = 0
        var addUpdateCount = 0
        var updatedFileArg: File? = null

        val remoteBook = BookFile(
            bookshelfId = bookshelfId,
            name = "vol1.cbz",
            parent = "/manga",
            path = path,
            size = 2048L,
            lastModifier = 500L,
            isHidden = false,
            totalPageCount = 0,
        )

        val bookshelfRepository = object : StubBookshelfRepository() {
            override fun flow(bookshelfId: BookshelfId): Flow<Bookshelf?> = flowOf(bookshelf)
        }

        val fileRepository = object : StubFileRepository() {
            override suspend fun updateSimple(
                file: File,
            ): Resource<File, FileRepositoryQueryError> {
                updateSimpleCount++
                updatedFileArg = file
                return Resource.Success(file)
            }

            override suspend fun addUpdate(file: File) {
                addUpdateCount++
            }
        }

        val remoteStorageClientFactory = object : RemoteStorageClient.Factory {
            override fun create(bookshelf: Bookshelf): RemoteStorageClient =
                object : StubRemoteStorageClient() {
                    override suspend fun file(path: String, resolveImageFolder: Boolean): File =
                        remoteBook

                    override suspend fun pageCount(book: Book): Int = 180
                }
        }

        val useCase = GetBookUseCaseImpl(
            bookshelfRepository = bookshelfRepository,
            fileRepository = fileRepository,
            remoteStorageClientFactory = remoteStorageClientFactory,
            settingsRepository = defaultSettingsRepository,
        )

        val result = useCase(GetBookUseCase.Request(bookshelfId, path)).first()

        assertTrue(result is Resource.Success)
        assertEquals(180, result.data.totalPageCount)
        assertEquals(1, updateSimpleCount)
        assertEquals(0, addUpdateCount)
        assertEquals(180, (updatedFileArg as Book).totalPageCount)
    }

    @Test
    fun getBook_whenPageCountCancelled_rethrowsCancellationException() = runTest {
        val bookshelfId = BookshelfId(1)
        val bookshelf = DeviceStorage(
            id = bookshelfId,
            displayName = "Test",
            fileCount = 0,
            isDeleted = false,
        )
        val path = "/manga/vol1.cbz"
        val remoteBook = BookFile(
            bookshelfId = bookshelfId,
            name = "vol1.cbz",
            parent = "/manga",
            path = path,
            size = 2048L,
            lastModifier = 500L,
            isHidden = false,
            totalPageCount = 0,
        )

        val bookshelfRepository = object : StubBookshelfRepository() {
            override fun flow(bookshelfId: BookshelfId): Flow<Bookshelf?> = flowOf(bookshelf)
        }

        val remoteStorageClientFactory = object : RemoteStorageClient.Factory {
            override fun create(bookshelf: Bookshelf): RemoteStorageClient =
                object : StubRemoteStorageClient() {
                    override suspend fun file(path: String, resolveImageFolder: Boolean): File =
                        remoteBook

                    override suspend fun pageCount(book: Book): Int =
                        throw CancellationException("Cancelled during pageCount")
                }
        }

        val useCase = GetBookUseCaseImpl(
            bookshelfRepository = bookshelfRepository,
            fileRepository = StubFileRepository(),
            remoteStorageClientFactory = remoteStorageClientFactory,
            settingsRepository = defaultSettingsRepository,
        )

        assertFailsWith<CancellationException> {
            useCase(GetBookUseCase.Request(bookshelfId, path)).first()
        }
    }

    @Test
    fun getBook_whenUpstreamFlowThrows_emitsReportedSystemError() = runTest {
        val bookshelfId = BookshelfId(1)
        val path = "/manga/vol1.cbz"

        val bookshelfRepository = object : StubBookshelfRepository() {
            override fun flow(bookshelfId: BookshelfId): Flow<Bookshelf?> = flow {
                throw IllegalStateException("Upstream flow failure")
            }
        }

        val remoteStorageClientFactory = object : RemoteStorageClient.Factory {
            override fun create(bookshelf: Bookshelf): RemoteStorageClient =
                StubRemoteStorageClient()
        }

        val useCase = GetBookUseCaseImpl(
            bookshelfRepository = bookshelfRepository,
            fileRepository = StubFileRepository(),
            remoteStorageClientFactory = remoteStorageClientFactory,
            settingsRepository = defaultSettingsRepository,
        )

        val result = useCase(GetBookUseCase.Request(bookshelfId, path)).first()
        assertEquals(Resource.Error(GetBookUseCase.Error.ReportedSystemError), result)
    }

    @Test
    fun getBook_whenRemoteFileThrowsNotFound_returnsNotFoundError() = runTest {
        val bookshelfId = BookshelfId(1)
        val bookshelf = DeviceStorage(bookshelfId, "Test", 0, false)
        val path = "/manga/missing.cbz"

        val bookshelfRepository = object : StubBookshelfRepository() {
            override fun flow(bookshelfId: BookshelfId): Flow<Bookshelf?> = flowOf(bookshelf)
        }

        val remoteStorageClientFactory = object : RemoteStorageClient.Factory {
            override fun create(bookshelf: Bookshelf): RemoteStorageClient =
                object : StubRemoteStorageClient() {
                    override suspend fun file(path: String, resolveImageFolder: Boolean): File =
                        throw RemoteException.NotFound()
                }
        }

        val useCase = GetBookUseCaseImpl(
            bookshelfRepository = bookshelfRepository,
            fileRepository = StubFileRepository(),
            remoteStorageClientFactory = remoteStorageClientFactory,
            settingsRepository = defaultSettingsRepository,
        )

        val result = useCase(GetBookUseCase.Request(bookshelfId, path)).first()
        assertEquals(Resource.Error(GetBookUseCase.Error.NotFound), result)
    }

    @Test
    fun getBook_whenRemoteFileThrowsNoNetwork_returnsReportedSystemError() = runTest {
        val bookshelfId = BookshelfId(1)
        val bookshelf = DeviceStorage(bookshelfId, "Test", 0, false)
        val path = "/manga/vol1.cbz"

        val bookshelfRepository = object : StubBookshelfRepository() {
            override fun flow(bookshelfId: BookshelfId): Flow<Bookshelf?> = flowOf(bookshelf)
        }

        val remoteStorageClientFactory = object : RemoteStorageClient.Factory {
            override fun create(bookshelf: Bookshelf): RemoteStorageClient =
                object : StubRemoteStorageClient() {
                    override suspend fun file(path: String, resolveImageFolder: Boolean): File =
                        throw RemoteException.NoNetwork()
                }
        }

        val useCase = GetBookUseCaseImpl(
            bookshelfRepository = bookshelfRepository,
            fileRepository = StubFileRepository(),
            remoteStorageClientFactory = remoteStorageClientFactory,
            settingsRepository = defaultSettingsRepository,
        )

        val result = useCase(GetBookUseCase.Request(bookshelfId, path)).first()
        assertEquals(Resource.Error(GetBookUseCase.Error.ReportedSystemError), result)
    }

    @Test
    fun getBook_whenPageCountThrowsNotFound_returnsNotFoundError() = runTest {
        val bookshelfId = BookshelfId(1)
        val bookshelf = DeviceStorage(bookshelfId, "Test", 0, false)
        val path = "/manga/deleted_during_fetch.cbz"
        val remoteBook = BookFile(
            bookshelfId = bookshelfId,
            name = "deleted_during_fetch.cbz",
            parent = "/manga",
            path = path,
            size = 2048L,
            lastModifier = 500L,
            isHidden = false,
            totalPageCount = 0,
        )

        val bookshelfRepository = object : StubBookshelfRepository() {
            override fun flow(bookshelfId: BookshelfId): Flow<Bookshelf?> = flowOf(bookshelf)
        }

        val remoteStorageClientFactory = object : RemoteStorageClient.Factory {
            override fun create(bookshelf: Bookshelf): RemoteStorageClient =
                object : StubRemoteStorageClient() {
                    override suspend fun file(path: String, resolveImageFolder: Boolean): File =
                        remoteBook

                    override suspend fun pageCount(book: Book): Int =
                        throw RemoteException.NotFound()
                }
        }

        val useCase = GetBookUseCaseImpl(
            bookshelfRepository = bookshelfRepository,
            fileRepository = StubFileRepository(),
            remoteStorageClientFactory = remoteStorageClientFactory,
            settingsRepository = defaultSettingsRepository,
        )

        val result = useCase(GetBookUseCase.Request(bookshelfId, path)).first()
        assertEquals(Resource.Error(GetBookUseCase.Error.NotFound), result)
    }
}
