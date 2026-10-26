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
import com.sorrowblue.comicviewer.domain.model.file.SupportExtension
import com.sorrowblue.comicviewer.domain.model.settings.FolderSettings
import com.sorrowblue.comicviewer.domain.model.storage.RemoteException
import com.sorrowblue.comicviewer.domain.repository.storage.RemoteStorageClient
import com.sorrowblue.comicviewer.domain.service.file.FileHierarchyScanService
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
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest

@OptIn(InternalDataApi::class)
class ScanBookshelfUseCaseTest {

    private val bookshelfId = BookshelfId(1)
    private val testBookshelf = DeviceStorage(bookshelfId, "TestShelf", 0, false)
    private val rootFolder = Folder(bookshelfId, "root", "", "/root", 0, 0, false)

    @Test
    fun testScanBookshelfUseCase_orchestration() = runTest {
        var scanHierarchyCalled = false
        val scannedFiles = mutableListOf<File>()
        val historyUpdated = mutableListOf<Pair<File, List<File>>>()

        val fakeScanService = object : FileHierarchyScanService {
            override suspend fun scanHierarchy(
                root: File,
                supportExtensions: List<String>,
                listFiles: suspend (file: File, filter: (File) -> Boolean) -> List<File>,
                onFolderFilesScanned: suspend (parent: File, files: List<File>) -> Unit,
                onFileDiscovered: suspend (file: File) -> Unit,
            ) {
                scanHierarchyCalled = true
                assertEquals(rootFolder, root)
                assertEquals(listOf("zip", "cbz"), supportExtensions)
                val book = BookFile(
                    bookshelfId,
                    "book.zip",
                    root.path,
                    "${root.path}/book.zip",
                    100,
                    0,
                    false,
                )
                val files = listFiles(root) { true }
                onFolderFilesScanned(root, files + book)
                onFileDiscovered(book)
            }
        }

        val fakeBookshelfRepository = object : StubBookshelfRepository() {
            override fun flow(bookshelfId: BookshelfId): Flow<Bookshelf?> = flowOf(testBookshelf)
        }

        val fakeFileRepository = object : StubFileRepository() {
            override suspend fun root(id: BookshelfId): Folder = rootFolder
            override suspend fun updateHistory(file: File, files: List<File>) {
                historyUpdated.add(file to files)
            }
        }

        val fakeClient = object : StubRemoteStorageClient() {
            override suspend fun listFiles(
                file: File,
                resolveImageFolder: Boolean,
                filter: (File) -> Boolean,
            ): List<File> = emptyList()
        }

        val fakeClientFactory = object : RemoteStorageClient.Factory {
            override fun create(bookshelf: Bookshelf): RemoteStorageClient = fakeClient
        }

        val fakeSettingsRepository = object : StubSettingsRepository() {
            override val folderSettings: Flow<FolderSettings> = flowOf(
                FolderSettings(
                    supportExtension = listOf(
                        SupportExtension.Archive.ZIP,
                        SupportExtension.Archive.CBZ,
                    ),
                    resolveImageFolder = false,
                ),
            )
        }

        val useCase = ScanBookshelfUseCaseImpl(
            bookshelfRepository = fakeBookshelfRepository,
            fileRepository = fakeFileRepository,
            remoteStorageClientFactory = fakeClientFactory,
            settingsRepository = fakeSettingsRepository,
            fileHierarchyScanService = fakeScanService,
        )

        val result = useCase(
            ScanBookshelfUseCase.Request(bookshelfId) { _, file ->
                scannedFiles.add(file)
            },
        )

        assertTrue(result is Resource.Success)
        assertTrue(scanHierarchyCalled)
        assertEquals(1, scannedFiles.size)
        assertEquals("book.zip", scannedFiles[0].name)
        assertEquals(1, historyUpdated.size)
        assertEquals(rootFolder, historyUpdated[0].first)
    }

    @Test
    fun testScanBookshelfUseCase_bookshelfNotFound() = runTest {
        var scanHierarchyCalled = false

        val fakeScanService = object : FileHierarchyScanService {
            override suspend fun scanHierarchy(
                root: File,
                supportExtensions: List<String>,
                listFiles: suspend (file: File, filter: (File) -> Boolean) -> List<File>,
                onFolderFilesScanned: suspend (parent: File, files: List<File>) -> Unit,
                onFileDiscovered: suspend (file: File) -> Unit,
            ) {
                scanHierarchyCalled = true
            }
        }

        val fakeBookshelfRepository = object : StubBookshelfRepository() {
            override fun flow(bookshelfId: BookshelfId): Flow<Bookshelf?> = flowOf(null)
        }

        val fakeFileRepository = object : StubFileRepository() {
            override suspend fun root(id: BookshelfId): Folder = rootFolder
        }

        val fakeClientFactory = object : RemoteStorageClient.Factory {
            override fun create(bookshelf: Bookshelf): RemoteStorageClient =
                object : StubRemoteStorageClient() {}
        }

        val fakeSettingsRepository = object : StubSettingsRepository() {}

        val useCase = ScanBookshelfUseCaseImpl(
            bookshelfRepository = fakeBookshelfRepository,
            fileRepository = fakeFileRepository,
            remoteStorageClientFactory = fakeClientFactory,
            settingsRepository = fakeSettingsRepository,
            fileHierarchyScanService = fakeScanService,
        )

        val result = useCase(
            ScanBookshelfUseCase.Request(bookshelfId) { _, _ -> },
        )

        assertTrue(result is Resource.Success)
        assertEquals(false, scanHierarchyCalled)
    }

    @Test
    fun testScanBookshelfUseCase_whenExceptionThrown_returnsSystemError() = runTest {
        val fakeScanService = object : FileHierarchyScanService {
            override suspend fun scanHierarchy(
                root: File,
                supportExtensions: List<String>,
                listFiles: suspend (file: File, filter: (File) -> Boolean) -> List<File>,
                onFolderFilesScanned: suspend (parent: File, files: List<File>) -> Unit,
                onFileDiscovered: suspend (file: File) -> Unit,
            ) {
                listFiles(root) { true }
            }
        }

        val fakeBookshelfRepository = object : StubBookshelfRepository() {
            override fun flow(bookshelfId: BookshelfId): Flow<Bookshelf?> = flowOf(testBookshelf)
        }

        val fakeFileRepository = object : StubFileRepository() {
            override suspend fun root(id: BookshelfId): Folder = rootFolder
        }

        val fakeClientFactory = object : RemoteStorageClient.Factory {
            override fun create(bookshelf: Bookshelf): RemoteStorageClient =
                object : StubRemoteStorageClient() {
                    override suspend fun listFiles(
                        file: File,
                        resolveImageFolder: Boolean,
                        filter: (File) -> Boolean,
                    ): List<File> = throw RemoteException.InvalidServer()
                }
        }

        val fakeSettingsRepository = object : StubSettingsRepository() {
            override val folderSettings: Flow<FolderSettings> = flowOf(FolderSettings())
        }

        val useCase = ScanBookshelfUseCaseImpl(
            bookshelfRepository = fakeBookshelfRepository,
            fileRepository = fakeFileRepository,
            remoteStorageClientFactory = fakeClientFactory,
            settingsRepository = fakeSettingsRepository,
            fileHierarchyScanService = fakeScanService,
        )

        val result = useCase(
            ScanBookshelfUseCase.Request(bookshelfId) { _, _ -> },
        )

        assertEquals(Resource.Error(ScanBookshelfUseCase.Error.System), result)
    }

    @Test
    fun testScanBookshelfUseCase_whenCancelled_rethrowsCancellationException() = runTest {
        val fakeScanService = object : FileHierarchyScanService {
            override suspend fun scanHierarchy(
                root: File,
                supportExtensions: List<String>,
                listFiles: suspend (file: File, filter: (File) -> Boolean) -> List<File>,
                onFolderFilesScanned: suspend (parent: File, files: List<File>) -> Unit,
                onFileDiscovered: suspend (file: File) -> Unit,
            ): Unit = throw CancellationException("Scan cancelled")
        }

        val fakeBookshelfRepository = object : StubBookshelfRepository() {
            override fun flow(bookshelfId: BookshelfId): Flow<Bookshelf?> = flowOf(testBookshelf)
        }

        val fakeFileRepository = object : StubFileRepository() {
            override suspend fun root(id: BookshelfId): Folder = rootFolder
        }

        val fakeClientFactory = object : RemoteStorageClient.Factory {
            override fun create(bookshelf: Bookshelf): RemoteStorageClient =
                object : StubRemoteStorageClient() {}
        }

        val fakeSettingsRepository = object : StubSettingsRepository() {
            override val folderSettings: Flow<FolderSettings> = flowOf(FolderSettings())
        }

        val useCase = ScanBookshelfUseCaseImpl(
            bookshelfRepository = fakeBookshelfRepository,
            fileRepository = fakeFileRepository,
            remoteStorageClientFactory = fakeClientFactory,
            settingsRepository = fakeSettingsRepository,
            fileHierarchyScanService = fakeScanService,
        )

        assertFailsWith<CancellationException> {
            useCase(ScanBookshelfUseCase.Request(bookshelfId) { _, _ -> })
        }
    }
}
