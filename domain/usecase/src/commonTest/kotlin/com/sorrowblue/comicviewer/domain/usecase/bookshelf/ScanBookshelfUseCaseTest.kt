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
import com.sorrowblue.comicviewer.domain.model.file.Book
import com.sorrowblue.comicviewer.domain.model.file.BookFile
import com.sorrowblue.comicviewer.domain.model.file.BookThumbnail
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.model.file.FileAttribute
import com.sorrowblue.comicviewer.domain.model.file.Folder
import com.sorrowblue.comicviewer.domain.model.file.SupportExtension
import com.sorrowblue.comicviewer.domain.model.search.SearchCondition
import com.sorrowblue.comicviewer.domain.model.settings.BookSettings
import com.sorrowblue.comicviewer.domain.model.settings.CollectionSettings
import com.sorrowblue.comicviewer.domain.model.settings.DisplaySettings
import com.sorrowblue.comicviewer.domain.model.settings.FolderSettings
import com.sorrowblue.comicviewer.domain.model.settings.SecuritySettings
import com.sorrowblue.comicviewer.domain.model.settings.Settings
import com.sorrowblue.comicviewer.domain.model.settings.ViewerSettings
import com.sorrowblue.comicviewer.domain.model.settings.folder.FolderDisplaySettings
import com.sorrowblue.comicviewer.domain.model.settings.folder.FolderThumbnailOrder
import com.sorrowblue.comicviewer.domain.model.settings.folder.SortType
import com.sorrowblue.comicviewer.domain.repository.BookshelfRepository
import com.sorrowblue.comicviewer.domain.repository.FileRepository
import com.sorrowblue.comicviewer.domain.repository.FileRepositoryQueryError
import com.sorrowblue.comicviewer.domain.repository.SettingsRepository
import com.sorrowblue.comicviewer.domain.repository.storage.RemoteStorageClient
import com.sorrowblue.comicviewer.domain.service.file.FileHierarchyScanService
import kotlin.test.Test
import kotlin.test.assertEquals
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
                resolveImageFolder: Boolean,
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
                    false
                )
                val files = listFiles(root) { true }
                onFolderFilesScanned(root, files + book)
                onFileDiscovered(book)
            }
        }

        val fakeBookshelfRepository = object : FakeBookshelfRepository() {
            override fun flow(bookshelfId: BookshelfId): Flow<Bookshelf?> = flowOf(testBookshelf)
        }

        val fakeFileRepository = object : FakeFileRepository() {
            override suspend fun root(id: BookshelfId): Folder = rootFolder
            override suspend fun updateHistory(file: File, files: List<File>) {
                historyUpdated.add(file to files)
            }
        }

        val fakeClient = object : FakeRemoteStorageClient() {
            override suspend fun listFiles(
                file: File,
                resolveImageFolder: Boolean,
                filter: (File) -> Boolean
            ): List<File> = emptyList()
        }

        val fakeClientFactory = object : RemoteStorageClient.Factory {
            override fun create(bookshelf: Bookshelf): RemoteStorageClient = fakeClient
        }

        val fakeSettingsRepository = object : FakeSettingsRepository() {
            override val folderSettings: Flow<FolderSettings> = flowOf(
                FolderSettings(
                    supportExtension = listOf(
                        SupportExtension.Archive.ZIP,
                        SupportExtension.Archive.CBZ
                    ),
                    resolveImageFolder = false,
                ),
            )
        }

        val useCase = ScanBookshelfUseCase(
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
                resolveImageFolder: Boolean,
                supportExtensions: List<String>,
                listFiles: suspend (file: File, filter: (File) -> Boolean) -> List<File>,
                onFolderFilesScanned: suspend (parent: File, files: List<File>) -> Unit,
                onFileDiscovered: suspend (file: File) -> Unit,
            ) {
                scanHierarchyCalled = true
            }
        }

        val fakeBookshelfRepository = object : FakeBookshelfRepository() {
            override fun flow(bookshelfId: BookshelfId): Flow<Bookshelf?> = flowOf(null)
        }

        val fakeFileRepository = object : FakeFileRepository() {
            override suspend fun root(id: BookshelfId): Folder = rootFolder
        }

        val fakeClientFactory = object : RemoteStorageClient.Factory {
            override fun create(bookshelf: Bookshelf): RemoteStorageClient =
                object : FakeRemoteStorageClient() {}
        }

        val fakeSettingsRepository = object : FakeSettingsRepository() {}

        val useCase = ScanBookshelfUseCase(
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
}

private open class FakeBookshelfRepository : BookshelfRepository {
    override suspend fun updateOrCreate(
        bookshelf: Bookshelf,
        transaction: suspend (Bookshelf) -> Unit
    ): Bookshelf? = TODO()
    override suspend fun delete(bookshelfId: BookshelfId): Resource<Unit, Resource.SystemError> =
        TODO()
    override fun flow(bookshelfId: BookshelfId): Flow<Bookshelf?> = TODO()
    override fun pagingSource(pagingConfig: PagingConfig): Flow<PagingData<BookshelfFolder>> =
        TODO()
    override fun allBookshelf(): Resource<Flow<List<Bookshelf>>, Resource.SystemError> = TODO()
    override suspend fun updateDeleted(bookshelfId: BookshelfId, isDeleted: Boolean) = TODO()
}

private open class FakeFileRepository : FileRepository {
    override fun pagingDataFlow(
        pagingConfig: PagingConfig,
        bookshelf: Bookshelf,
        file: File,
        searchCondition: () -> SearchCondition
    ): Flow<PagingData<File>> = TODO()
    override fun pagingSourceBookThumbnail(
        pagingConfig: PagingConfig,
        bookshelf: Bookshelf,
        file: File,
        searchCondition: () -> SearchCondition
    ): Flow<PagingData<BookThumbnail>> = TODO()
    override fun pagingDataFlow(
        pagingConfig: PagingConfig,
        bookshelfId: BookshelfId?,
        searchCondition: () -> SearchCondition
    ): Flow<PagingData<File>> = TODO()
    override suspend fun addUpdate(fileModel: File) = TODO()
    override suspend fun updateHistory(
        path: String,
        bookshelfId: BookshelfId,
        lastReadPage: Int,
        lastReading: Long
    ) = TODO()
    override suspend fun updateAdditionalInfo(
        path: String,
        bookshelfId: BookshelfId,
        cacheKey: String,
        totalPage: Int
    ) = TODO()
    override suspend fun updateSimpleAll(list: List<File>) = TODO()
    override suspend fun updateSimple(list: File): Resource<File, FileRepositoryQueryError> = TODO()
    override suspend fun selectByNotPaths(
        bookshelfId: BookshelfId,
        path: String,
        list: List<String>
    ): List<File> = TODO()
    override suspend fun deleteAll(list: List<File>) = TODO()
    override suspend fun exists(bookshelfId: BookshelfId, path: String): Boolean = TODO()
    override fun pagingSource(
        bookshelfId: BookshelfId,
        pagingConfig: PagingConfig
    ): Flow<PagingData<BookThumbnail>> = TODO()
    override fun flow(bookshelfId: BookshelfId, path: String): Flow<File?> = TODO()
    override suspend fun findBy(bookshelfId: BookshelfId, path: String): File? = TODO()
    override fun nextFileModel(
        bookshelfId: BookshelfId,
        path: String,
        sortType: SortType
    ): Flow<File?> = TODO()
    override fun nextFileModel(
        bookshelfId: BookshelfId,
        path: String,
        searchCondition: SearchCondition,
        sortType: SortType
    ): Flow<File?> = TODO()
    override fun prevFileModel(
        bookshelfId: BookshelfId,
        path: String,
        sortType: SortType
    ): Flow<File?> = TODO()
    override fun prevFileModel(
        bookshelfId: BookshelfId,
        path: String,
        searchCondition: SearchCondition,
        sortType: SortType
    ): Flow<File?> = TODO()
    override suspend fun getCacheKeys(
        bookshelfId: BookshelfId,
        parent: String,
        limit: Int,
        folderThumbnailOrderModel: FolderThumbnailOrder
    ): List<String> = TODO()
    override suspend fun removeCacheKey(diskCacheKey: String) = TODO()
    override suspend fun root(id: BookshelfId): Folder? = TODO()
    override fun pagingHistoryBookSource(pagingConfig: PagingConfig): Flow<PagingData<Book>> =
        TODO()
    override suspend fun deleteThumbnails() = TODO()
    override suspend fun clearCacheKey(bookshelfId: BookshelfId) = TODO()
    override suspend fun deleteHistory(bookshelfId: BookshelfId, list: List<String>): Unit = TODO()
    override suspend fun deleteAllHistory(): Unit = TODO()
    override suspend fun updateHistory(file: File, files: List<File>): Unit = TODO()
    override suspend fun deleteAll2(bookshelfModelId: BookshelfId): Unit = TODO()
    override suspend fun getCacheKeyList(bookshelfId: BookshelfId): List<String> = TODO()
    override fun lastHistory(): Flow<File?> = TODO()
    override suspend fun fileList(bookshelfId: BookshelfId, limit: Int, offset: Long): List<File> =
        TODO()
    override suspend fun count(bookshelfId: BookshelfId): Long = TODO()
    override suspend fun updateFileType(file: File) = TODO()
}

private open class FakeRemoteStorageClient : RemoteStorageClient {
    override suspend fun connect(path: String) = TODO()
    override suspend fun exists(path: String): Boolean = TODO()
    override suspend fun listFiles(
        file: File,
        resolveImageFolder: Boolean,
        filter: (File) -> Boolean
    ): List<File> = TODO()
    override suspend fun file(path: String, resolveImageFolder: Boolean): File = TODO()
    override suspend fun pageCount(book: Book): Int = TODO()
    override suspend fun getAttribute(path: String): FileAttribute? = TODO()
    override suspend fun getFileSize(path: String): Long = TODO()
}

private open class FakeSettingsRepository : SettingsRepository {
    override val settings: Flow<Settings> get() = TODO()
    override suspend fun updateSettings(transform: suspend (Settings) -> Settings): Settings =
        TODO()
    override val displaySettings: Flow<DisplaySettings> get() = TODO()
    override suspend fun updateDisplaySettings(
        transform: suspend (DisplaySettings) -> DisplaySettings
    ): DisplaySettings = TODO()
    override val viewerSettings: Flow<ViewerSettings> get() = TODO()
    override suspend fun updateViewerSettings(
        transform: suspend (ViewerSettings) -> ViewerSettings
    ): ViewerSettings = TODO()
    override val bookSettings: Flow<BookSettings> get() = TODO()
    override suspend fun updateBookSettings(
        transform: suspend (BookSettings) -> BookSettings
    ): BookSettings = TODO()
    override val folderDisplaySettings: Flow<FolderDisplaySettings> get() = TODO()
    override suspend fun updateFolderDisplaySettings(
        transform: suspend (FolderDisplaySettings) -> FolderDisplaySettings
    ): FolderDisplaySettings = TODO()
    override val folderSettings: Flow<FolderSettings> get() = TODO()
    override suspend fun updateFolderSettings(
        transform: suspend (FolderSettings) -> FolderSettings
    ): FolderSettings = TODO()
    override val securitySettings: Flow<SecuritySettings> get() = TODO()
    override suspend fun updateSecuritySettings(
        transform: suspend (SecuritySettings) -> SecuritySettings
    ): SecuritySettings = TODO()
    override val collectionSettings: Flow<CollectionSettings> get() = TODO()
    override suspend fun updateCollectionSettings(
        transform: suspend (CollectionSettings) -> CollectionSettings
    ): CollectionSettings = TODO()
}
