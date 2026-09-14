/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.usecase.file

import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.sorrowblue.comicviewer.domain.model.bookshelf.Bookshelf
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.collection.BasicCollection
import com.sorrowblue.comicviewer.domain.model.collection.Collection
import com.sorrowblue.comicviewer.domain.model.collection.CollectionCriteria
import com.sorrowblue.comicviewer.domain.model.collection.CollectionFile
import com.sorrowblue.comicviewer.domain.model.collection.CollectionId
import com.sorrowblue.comicviewer.domain.model.collection.SmartCollection
import com.sorrowblue.comicviewer.domain.model.common.InternalDataApi
import com.sorrowblue.comicviewer.domain.model.common.Resource
import com.sorrowblue.comicviewer.domain.model.file.Book
import com.sorrowblue.comicviewer.domain.model.file.BookFile
import com.sorrowblue.comicviewer.domain.model.file.BookThumbnail
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.model.file.Folder
import com.sorrowblue.comicviewer.domain.model.search.SearchCondition
import com.sorrowblue.comicviewer.domain.model.settings.BookSettings
import com.sorrowblue.comicviewer.domain.model.settings.CollectionSettings
import com.sorrowblue.comicviewer.domain.model.settings.DisplaySettings
import com.sorrowblue.comicviewer.domain.model.settings.FolderSettings
import com.sorrowblue.comicviewer.domain.model.settings.SecuritySettings
import com.sorrowblue.comicviewer.domain.model.settings.Settings
import com.sorrowblue.comicviewer.domain.model.settings.ViewerSettings
import com.sorrowblue.comicviewer.domain.model.settings.folder.FolderDisplaySettings
import com.sorrowblue.comicviewer.domain.model.settings.folder.FolderScopeOnly
import com.sorrowblue.comicviewer.domain.model.settings.folder.FolderThumbnailOrder
import com.sorrowblue.comicviewer.domain.model.settings.folder.SortType
import com.sorrowblue.comicviewer.domain.repository.CollectionFileRepository
import com.sorrowblue.comicviewer.domain.repository.CollectionRepository
import com.sorrowblue.comicviewer.domain.repository.FileRepository
import com.sorrowblue.comicviewer.domain.repository.FileRepositoryQueryError
import com.sorrowblue.comicviewer.domain.repository.SettingsRepository
import com.sorrowblue.comicviewer.domain.service.book.BookNavigationService
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest

@OptIn(InternalDataApi::class)
class GetNextBookUseCaseTest {

    private val bookshelfId = BookshelfId(1)
    private val book1 = BookFile(
        bookshelfId = bookshelfId,
        name = "01.cbz",
        parent = "/manga",
        path = "/manga/01.cbz",
        size = 100,
        lastModifier = 10,
        isHidden = false,
    )
    private val book2 = BookFile(
        bookshelfId = bookshelfId,
        name = "02.cbz",
        parent = "/manga",
        path = "/manga/02.cbz",
        size = 200,
        lastModifier = 20,
        isHidden = false,
    )

    @Test
    fun testFolder_usesFolderScopedSortType() = runTest {
        var requestedSortType: SortType? = null

        val fakeSettingsRepository = FakeSettingsRepository(
            FolderDisplaySettings(
                sortType = SortType.Name(true),
                folderScopeOnlyList = listOf(
                    FolderScopeOnly(
                        bookshelfId = bookshelfId,
                        path = "/manga",
                        sortType = SortType.Date(false),
                    ),
                ),
            ),
        )

        val fakeFileRepository = object : FakeFileRepository() {
            override suspend fun findBy(bookshelfId: BookshelfId, path: String): File? = book1

            override fun nextFileModel(
                bookshelfId: BookshelfId,
                path: String,
                sortType: SortType,
            ): Flow<File?> {
                requestedSortType = sortType
                return flowOf(book2)
            }
        }

        val useCase = GetNextBookUseCase(
            settingsRepository = fakeSettingsRepository,
            fileRepository = fakeFileRepository,
            collectionFileRepository = object : FakeCollectionFileRepository() {},
            collectionRepository = object : FakeCollectionRepository() {},
            bookNavigationService = BookNavigationService(),
        )

        val result = useCase(
            GetNextBookUseCase.Request(
                bookshelfId = bookshelfId,
                path = book1.path,
                location = GetNextBookUseCase.Location.Folder,
                isNext = true,
            ),
        )

        assertTrue(result is Resource.Success)
        assertEquals(book2, result.data)
        assertEquals(SortType.Date(false), requestedSortType)
    }

    @Test
    fun testFolder_notFoundWhenFileNotBook() = runTest {
        val nonBookFolder = Folder(
            bookshelfId = bookshelfId,
            name = "subfolder",
            parent = "/manga",
            path = "/manga/subfolder",
            size = 0,
            lastModifier = 0,
            isHidden = false,
        )

        val fakeFileRepository = object : FakeFileRepository() {
            override suspend fun findBy(bookshelfId: BookshelfId, path: String): File? = book1
            override fun nextFileModel(
                bookshelfId: BookshelfId,
                path: String,
                sortType: SortType,
            ): Flow<File?> = flowOf(nonBookFolder)
        }

        val useCase = GetNextBookUseCase(
            settingsRepository = FakeSettingsRepository(),
            fileRepository = fakeFileRepository,
            collectionFileRepository = object : FakeCollectionFileRepository() {},
            collectionRepository = object : FakeCollectionRepository() {},
            bookNavigationService = BookNavigationService(),
        )

        val result = useCase(
            GetNextBookUseCase.Request(
                bookshelfId = bookshelfId,
                path = book1.path,
                location = GetNextBookUseCase.Location.Folder,
                isNext = true,
            ),
        )

        assertTrue(result is Resource.Error)
        assertEquals(GetNextBookUseCase.Error.NotFound, result.error)
    }

    @Test
    fun testBasicCollection_usesGlobalSortType() = runTest {
        val collectionId = CollectionId(10)
        val basicCollection = BasicCollection(name = "Favorites")
        var requestedSortType: SortType? = null

        val fakeSettingsRepository = FakeSettingsRepository(
            FolderDisplaySettings(sortType = SortType.Name(false)),
        )

        val fakeCollectionRepository = object : FakeCollectionRepository() {
            override fun flow(id: CollectionId): Flow<Collection?> = flowOf(basicCollection)
        }

        val fakeCollectionFileRepository = object : FakeCollectionFileRepository() {
            override fun flowNextCollectionFile(
                file: CollectionFile,
                sortType: SortType,
            ): Flow<File?> {
                requestedSortType = sortType
                return flowOf(book2)
            }
        }

        val useCase = GetNextBookUseCase(
            settingsRepository = fakeSettingsRepository,
            fileRepository = object : FakeFileRepository() {},
            collectionFileRepository = fakeCollectionFileRepository,
            collectionRepository = fakeCollectionRepository,
            bookNavigationService = BookNavigationService(),
        )

        val result = useCase(
            GetNextBookUseCase.Request(
                bookshelfId = bookshelfId,
                path = book1.path,
                location = GetNextBookUseCase.Location.Collection(collectionId),
                isNext = true,
            ),
        )

        assertTrue(result is Resource.Success)
        assertEquals(book2, result.data)
        assertEquals(SortType.Name(false), requestedSortType)
    }

    @Test
    fun testSmartCollection_usesSearchConditionSortType() = runTest {
        val collectionId = CollectionId(20)
        val smartCollection = SmartCollection(
            name = "Recent Smart",
            bookshelfId = bookshelfId,
            searchCondition = SearchCondition(
                sortType = SortType.Date(false),
            ),
        )
        var requestedSortType: SortType? = null

        val fakeSettingsRepository = FakeSettingsRepository(
            FolderDisplaySettings(sortType = SortType.Name(true)),
        )

        val fakeCollectionRepository = object : FakeCollectionRepository() {
            override fun flow(id: CollectionId): Flow<Collection?> = flowOf(smartCollection)
        }

        val fakeFileRepository = object : FakeFileRepository() {
            override fun nextFileModel(
                bookshelfId: BookshelfId,
                path: String,
                searchCondition: SearchCondition,
                sortType: SortType,
            ): Flow<File?> {
                requestedSortType = sortType
                return flowOf(book2)
            }
        }

        val useCase = GetNextBookUseCase(
            settingsRepository = fakeSettingsRepository,
            fileRepository = fakeFileRepository,
            collectionFileRepository = object : FakeCollectionFileRepository() {},
            collectionRepository = fakeCollectionRepository,
            bookNavigationService = BookNavigationService(),
        )

        val result = useCase(
            GetNextBookUseCase.Request(
                bookshelfId = bookshelfId,
                path = book1.path,
                location = GetNextBookUseCase.Location.Collection(collectionId),
                isNext = true,
            ),
        )

        assertTrue(result is Resource.Success)
        assertEquals(book2, result.data)
        assertEquals(SortType.Date(false), requestedSortType)
    }

    @Test
    fun testCollection_notFoundWhenCollectionDoesNotExist() = runTest {
        val collectionId = CollectionId(999)

        val fakeCollectionRepository = object : FakeCollectionRepository() {
            override fun flow(id: CollectionId): Flow<Collection?> = flowOf(null)
        }

        val useCase = GetNextBookUseCase(
            settingsRepository = FakeSettingsRepository(),
            fileRepository = object : FakeFileRepository() {},
            collectionFileRepository = object : FakeCollectionFileRepository() {},
            collectionRepository = fakeCollectionRepository,
            bookNavigationService = BookNavigationService(),
        )

        val result = useCase(
            GetNextBookUseCase.Request(
                bookshelfId = bookshelfId,
                path = book1.path,
                location = GetNextBookUseCase.Location.Collection(collectionId),
                isNext = true,
            ),
        )

        assertTrue(result is Resource.Error)
        assertEquals(GetNextBookUseCase.Error.NotFound, result.error)
    }
}

private open class FakeSettingsRepository(
    initialFolderDisplaySettings: FolderDisplaySettings = FolderDisplaySettings(),
) : SettingsRepository {
    override val folderDisplaySettings: Flow<FolderDisplaySettings> = flowOf(
        initialFolderDisplaySettings,
    )

    override suspend fun updateFolderDisplaySettings(
        transform: suspend (FolderDisplaySettings) -> FolderDisplaySettings,
    ): FolderDisplaySettings = TODO()

    override val settings: Flow<Settings> get() = TODO()
    override suspend fun updateSettings(transform: suspend (Settings) -> Settings): Settings =
        TODO()
    override val displaySettings: Flow<DisplaySettings> get() = TODO()
    override suspend fun updateDisplaySettings(
        transform: suspend (DisplaySettings) -> DisplaySettings,
    ): DisplaySettings = TODO()
    override val viewerSettings: Flow<ViewerSettings> get() = TODO()
    override suspend fun updateViewerSettings(
        transform: suspend (ViewerSettings) -> ViewerSettings,
    ): ViewerSettings = TODO()
    override val bookSettings: Flow<BookSettings> get() = TODO()
    override suspend fun updateBookSettings(
        transform: suspend (BookSettings) -> BookSettings,
    ): BookSettings = TODO()
    override val folderSettings: Flow<FolderSettings> get() = TODO()
    override suspend fun updateFolderSettings(
        transform: suspend (FolderSettings) -> FolderSettings,
    ): FolderSettings = TODO()
    override val securitySettings: Flow<SecuritySettings> get() = TODO()
    override suspend fun updateSecuritySettings(
        transform: suspend (SecuritySettings) -> SecuritySettings,
    ): SecuritySettings = TODO()
    override val collectionSettings: Flow<CollectionSettings> get() = TODO()
    override suspend fun updateCollectionSettings(
        transform: suspend (CollectionSettings) -> CollectionSettings,
    ): CollectionSettings = TODO()
}

private open class FakeFileRepository : FileRepository {
    override fun pagingDataFlow(
        pagingConfig: PagingConfig,
        bookshelf: Bookshelf,
        file: File,
        searchCondition: () -> SearchCondition,
    ): Flow<PagingData<File>> = TODO()
    override fun pagingSourceBookThumbnail(
        pagingConfig: PagingConfig,
        bookshelf: Bookshelf,
        file: File,
        searchCondition: () -> SearchCondition,
    ): Flow<PagingData<BookThumbnail>> = TODO()
    override fun pagingDataFlow(
        pagingConfig: PagingConfig,
        bookshelfId: BookshelfId?,
        searchCondition: () -> SearchCondition,
    ): Flow<PagingData<File>> = TODO()
    override suspend fun addUpdate(fileModel: File) = TODO()
    override suspend fun updateHistory(
        path: String,
        bookshelfId: BookshelfId,
        lastReadPage: Int,
        lastReading: Long,
    ) = TODO()
    override suspend fun updateAdditionalInfo(
        path: String,
        bookshelfId: BookshelfId,
        cacheKey: String,
        totalPage: Int,
    ) = TODO()
    override suspend fun updateSimpleAll(list: List<File>) = TODO()
    override suspend fun updateSimple(list: File): Resource<File, FileRepositoryQueryError> = TODO()
    override suspend fun selectByNotPaths(
        bookshelfId: BookshelfId,
        path: String,
        list: List<String>,
    ): List<File> = TODO()
    override suspend fun deleteAll(list: List<File>) = TODO()
    override suspend fun exists(bookshelfId: BookshelfId, path: String): Boolean = TODO()
    override fun pagingSource(
        bookshelfId: BookshelfId,
        pagingConfig: PagingConfig,
    ): Flow<PagingData<BookThumbnail>> = TODO()
    override fun flow(bookshelfId: BookshelfId, path: String): Flow<File?> = TODO()
    override suspend fun findBy(bookshelfId: BookshelfId, path: String): File? = TODO()
    override fun nextFileModel(
        bookshelfId: BookshelfId,
        path: String,
        sortType: SortType,
    ): Flow<File?> = TODO()
    override fun nextFileModel(
        bookshelfId: BookshelfId,
        path: String,
        searchCondition: SearchCondition,
        sortType: SortType,
    ): Flow<File?> = TODO()
    override fun prevFileModel(
        bookshelfId: BookshelfId,
        path: String,
        sortType: SortType,
    ): Flow<File?> = TODO()
    override fun prevFileModel(
        bookshelfId: BookshelfId,
        path: String,
        searchCondition: SearchCondition,
        sortType: SortType,
    ): Flow<File?> = TODO()
    override suspend fun getCacheKeys(
        bookshelfId: BookshelfId,
        parent: String,
        limit: Int,
        folderThumbnailOrderModel: FolderThumbnailOrder,
    ): List<String> = TODO()
    override suspend fun removeCacheKey(diskCacheKey: String) = TODO()
    override suspend fun root(id: BookshelfId): Folder? = TODO()
    override fun pagingHistoryBookSource(pagingConfig: PagingConfig): Flow<PagingData<Book>> =
        TODO()
    override suspend fun deleteThumbnails() = TODO()
    override suspend fun clearCacheKey(bookshelfId: BookshelfId) = TODO()
    override suspend fun deleteHistory(bookshelfId: BookshelfId, list: List<String>) = TODO()
    override suspend fun deleteAllHistory() = TODO()
    override suspend fun updateHistory(file: File, files: List<File>) = TODO()
    override suspend fun deleteAll2(bookshelfModelId: BookshelfId) = TODO()
    override suspend fun getCacheKeyList(bookshelfId: BookshelfId): List<String> = TODO()
    override fun lastHistory(): Flow<File?> = TODO()
    override suspend fun fileList(bookshelfId: BookshelfId, limit: Int, offset: Long): List<File> =
        TODO()
    override suspend fun count(bookshelfId: BookshelfId): Long = TODO()
    override suspend fun updateFileType(file: File) = TODO()
}

private open class FakeCollectionRepository : CollectionRepository {
    override fun pagingDataFlow(pagingConfig: PagingConfig): Flow<PagingData<Collection>> = TODO()
    override fun pagingDataFlow(
        pagingConfig: PagingConfig,
        bookshelfId: BookshelfId,
        path: String,
        collectionCriteria: () -> CollectionCriteria,
    ): Flow<PagingData<Pair<Collection, Boolean>>> = TODO()
    override fun flow(id: CollectionId): Flow<Collection?> = TODO()
    override suspend fun create(collection: Collection): Collection = TODO()
    override suspend fun update(collection: Collection) = TODO()
    override suspend fun delete(collectionId: CollectionId) = TODO()
}

private open class FakeCollectionFileRepository : CollectionFileRepository {
    override fun pagingDataFlow(
        id: CollectionId,
        pagingConfig: PagingConfig,
        sortType: () -> SortType,
    ): Flow<PagingData<File>> = TODO()
    override suspend fun add(file: CollectionFile) = TODO()
    override suspend fun remove(file: CollectionFile) = TODO()
    override fun flowNextCollectionFile(file: CollectionFile, sortType: SortType): Flow<File?> =
        TODO()
    override fun flowPrevCollectionFile(file: CollectionFile, sortType: SortType): Flow<File?> =
        TODO()
    override suspend fun getCacheKeyList(
        id: CollectionId,
        limit: Int,
    ): List<Pair<BookshelfId, String>> = TODO()
}
