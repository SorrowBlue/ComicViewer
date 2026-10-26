/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.usecase.bookshelf

import com.sorrowblue.comicviewer.domain.model.bookshelf.Bookshelf
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.bookshelf.DeviceStorage
import com.sorrowblue.comicviewer.domain.model.common.InternalDataApi
import com.sorrowblue.comicviewer.domain.model.common.Resource
import com.sorrowblue.comicviewer.domain.model.file.BookFile
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.model.file.Folder
import com.sorrowblue.comicviewer.domain.model.storage.RemoteException
import com.sorrowblue.comicviewer.domain.repository.storage.RemoteStorageClient
import com.sorrowblue.comicviewer.domain.usecase.StubBookshelfRepository
import com.sorrowblue.comicviewer.domain.usecase.StubFileRepository
import com.sorrowblue.comicviewer.domain.usecase.StubImageCacheRepository
import com.sorrowblue.comicviewer.domain.usecase.StubRemoteStorageClient
import kotlin.coroutines.cancellation.CancellationException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue
import kotlinx.coroutines.test.runTest

@OptIn(InternalDataApi::class)
class RegisterBookshelfUseCaseTest {

    @Test
    fun registerNewBookshelf_fetchesRemoteFileOnceAndSavesRootWithNewBookshelfId() = runTest {
        val initialBookshelf = DeviceStorage(displayName = "NewShelf")
        val assignedId = BookshelfId(42)
        val path = "/storage/emulated/0/Manga"

        var clientCreatedCount = 0
        var fileFetchedCount = 0
        var savedFile: File? = null

        val remoteClientFactory = object : RemoteStorageClient.Factory {
            override fun create(bookshelf: Bookshelf): RemoteStorageClient {
                clientCreatedCount++
                return object : StubRemoteStorageClient() {
                    override suspend fun connect(path: String) = Unit

                    override suspend fun file(path: String, resolveImageFolder: Boolean): File {
                        fileFetchedCount++
                        return Folder(
                            bookshelfId = bookshelf.id,
                            name = "Manga",
                            parent = "/storage/emulated/0",
                            path = path,
                            size = 0,
                            lastModifier = 100L,
                            isHidden = false,
                        )
                    }
                }
            }
        }

        val bookshelfRepository = object : StubBookshelfRepository() {
            override suspend fun updateOrCreate(
                bookshelf: Bookshelf,
                transaction: suspend (Bookshelf) -> Unit,
            ): Bookshelf {
                val created = DeviceStorage(
                    id = assignedId,
                    displayName = bookshelf.displayName,
                    fileCount = bookshelf.fileCount,
                    isDeleted = bookshelf.isDeleted,
                )
                transaction(created)
                return created
            }
        }

        val fileRepository = object : StubFileRepository() {
            override suspend fun root(id: BookshelfId): Folder? = null
            override suspend fun addUpdate(fileModel: File) {
                savedFile = fileModel
            }
        }

        val useCase = RegisterBookshelfUseCaseImpl(
            fileRepository = fileRepository,
            bookshelfRepository = bookshelfRepository,
            remoteStorageClientFactory = remoteClientFactory,
            imageCacheRepository = StubImageCacheRepository(),
        )

        val result = useCase(RegisterBookshelfUseCase.Request(initialBookshelf, path))

        assertTrue(result is Resource.Success)
        assertEquals(assignedId, result.data.id)
        assertEquals(1, clientCreatedCount)
        assertEquals(1, fileFetchedCount)
        val rootFolder = savedFile as Folder
        assertEquals(assignedId, rootFolder.bookshelfId)
        assertEquals("", rootFolder.parent)
        assertEquals(path, rootFolder.path)
    }

    @Test
    fun registerBookshelf_whenConnectCancelled_rethrowsCancellationException() = runTest {
        val initialBookshelf = DeviceStorage(displayName = "NewShelf")
        val remoteClientFactory = object : RemoteStorageClient.Factory {
            override fun create(bookshelf: Bookshelf): RemoteStorageClient =
                object : StubRemoteStorageClient() {
                    override suspend fun connect(path: String): Unit =
                        throw CancellationException("Cancelled during connect")
                }
        }

        val useCase = RegisterBookshelfUseCaseImpl(
            fileRepository = StubFileRepository(),
            bookshelfRepository = StubBookshelfRepository(),
            remoteStorageClientFactory = remoteClientFactory,
            imageCacheRepository = StubImageCacheRepository(),
        )

        assertFailsWith<CancellationException> {
            useCase(RegisterBookshelfUseCase.Request(initialBookshelf, "/Manga"))
        }
    }

    @Test
    fun registerBookshelf_whenConnectThrowsUnexpectedException_returnsSystemError() = runTest {
        val initialBookshelf = DeviceStorage(displayName = "NewShelf")
        val remoteClientFactory = object : RemoteStorageClient.Factory {
            override fun create(bookshelf: Bookshelf): RemoteStorageClient =
                object : StubRemoteStorageClient() {
                    override suspend fun connect(path: String): Unit =
                        throw IllegalStateException("Unexpected failure")
                }
        }

        val useCase = RegisterBookshelfUseCaseImpl(
            fileRepository = StubFileRepository(),
            bookshelfRepository = StubBookshelfRepository(),
            remoteStorageClientFactory = remoteClientFactory,
            imageCacheRepository = StubImageCacheRepository(),
        )

        val result = useCase(RegisterBookshelfUseCase.Request(initialBookshelf, "/Manga"))

        assertTrue(result is Resource.Error)
        assertEquals(RegisterBookshelfUseCase.Error.System, result.error)
    }

    @Test
    fun registerBookshelf_whenTargetIsNotFolder_returnsPathError() = runTest {
        val initialBookshelf = DeviceStorage(displayName = "NewShelf")
        val path = "/storage/emulated/0/Manga/vol1.cbz"
        val remoteClientFactory = object : RemoteStorageClient.Factory {
            override fun create(bookshelf: Bookshelf): RemoteStorageClient =
                object : StubRemoteStorageClient() {
                    override suspend fun connect(path: String) = Unit

                    override suspend fun file(path: String, resolveImageFolder: Boolean): File =
                        BookFile(
                            bookshelfId = bookshelf.id,
                            name = "vol1.cbz",
                            parent = "/storage/emulated/0/Manga",
                            path = path,
                            size = 1024L,
                            lastModifier = 100L,
                            isHidden = false,
                        )
                }
        }

        val useCase = RegisterBookshelfUseCaseImpl(
            fileRepository = StubFileRepository(),
            bookshelfRepository = StubBookshelfRepository(),
            remoteStorageClientFactory = remoteClientFactory,
            imageCacheRepository = StubImageCacheRepository(),
        )

        val result = useCase(RegisterBookshelfUseCase.Request(initialBookshelf, path))

        assertTrue(result is Resource.Error)
        assertEquals(RegisterBookshelfUseCase.Error.Path, result.error)
    }

    @Test
    fun registerBookshelf_whenUpdateOrCreateReturnsNull_returnsSystemError() = runTest {
        val initialBookshelf = DeviceStorage(displayName = "NewShelf")
        val path = "/storage/emulated/0/Manga"
        val remoteClientFactory = object : RemoteStorageClient.Factory {
            override fun create(bookshelf: Bookshelf): RemoteStorageClient =
                object : StubRemoteStorageClient() {
                    override suspend fun connect(path: String) = Unit

                    override suspend fun file(path: String, resolveImageFolder: Boolean): File =
                        Folder(
                            bookshelfId = bookshelf.id,
                            name = "Manga",
                            parent = "/storage/emulated/0",
                            path = path,
                            size = 0,
                            lastModifier = 100L,
                            isHidden = false,
                        )
                }
        }

        val bookshelfRepository = object : StubBookshelfRepository() {
            override suspend fun updateOrCreate(
                bookshelf: Bookshelf,
                transaction: suspend (Bookshelf) -> Unit,
            ): Bookshelf? = null
        }

        val fileRepository = object : StubFileRepository() {
            override suspend fun root(id: BookshelfId): Folder? = null
        }

        val useCase = RegisterBookshelfUseCaseImpl(
            fileRepository = fileRepository,
            bookshelfRepository = bookshelfRepository,
            remoteStorageClientFactory = remoteClientFactory,
            imageCacheRepository = StubImageCacheRepository(),
        )

        val result = useCase(RegisterBookshelfUseCase.Request(initialBookshelf, path))

        assertTrue(result is Resource.Error)
        assertEquals(RegisterBookshelfUseCase.Error.System, result.error)
    }

    @Test
    fun registerBookshelf_whenClientFileThrowsNotFound_returnsPathError() = runTest {
        val initialBookshelf = DeviceStorage(displayName = "NewShelf")
        val path = "/storage/emulated/0/Missing"
        val remoteClientFactory = object : RemoteStorageClient.Factory {
            override fun create(bookshelf: Bookshelf): RemoteStorageClient =
                object : StubRemoteStorageClient() {
                    override suspend fun connect(path: String) = Unit

                    override suspend fun file(path: String, resolveImageFolder: Boolean): File =
                        throw RemoteException.NotFound()
                }
        }

        val useCase = RegisterBookshelfUseCaseImpl(
            fileRepository = StubFileRepository(),
            bookshelfRepository = StubBookshelfRepository(),
            remoteStorageClientFactory = remoteClientFactory,
            imageCacheRepository = StubImageCacheRepository(),
        )

        val result = useCase(RegisterBookshelfUseCase.Request(initialBookshelf, path))

        assertTrue(result is Resource.Error)
        assertEquals(RegisterBookshelfUseCase.Error.Path, result.error)
    }

    @Test
    fun registerBookshelf_whenRepositoryThrowsException_returnsSystemError() = runTest {
        val initialBookshelf = DeviceStorage(displayName = "NewShelf")
        val path = "/storage/emulated/0/Manga"
        val remoteClientFactory = object : RemoteStorageClient.Factory {
            override fun create(bookshelf: Bookshelf): RemoteStorageClient =
                object : StubRemoteStorageClient() {
                    override suspend fun connect(path: String) = Unit

                    override suspend fun file(path: String, resolveImageFolder: Boolean): File =
                        Folder(
                            bookshelfId = bookshelf.id,
                            name = "Manga",
                            parent = "/storage/emulated/0",
                            path = path,
                            size = 0,
                            lastModifier = 100L,
                            isHidden = false,
                        )
                }
        }

        val fileRepository = object : StubFileRepository() {
            override suspend fun root(id: BookshelfId): Folder? =
                throw IllegalStateException("Database failure")
        }

        val useCase = RegisterBookshelfUseCaseImpl(
            fileRepository = fileRepository,
            bookshelfRepository = StubBookshelfRepository(),
            remoteStorageClientFactory = remoteClientFactory,
            imageCacheRepository = StubImageCacheRepository(),
        )

        val result = useCase(RegisterBookshelfUseCase.Request(initialBookshelf, path))

        assertTrue(result is Resource.Error)
        assertEquals(RegisterBookshelfUseCase.Error.System, result.error)
    }
}
