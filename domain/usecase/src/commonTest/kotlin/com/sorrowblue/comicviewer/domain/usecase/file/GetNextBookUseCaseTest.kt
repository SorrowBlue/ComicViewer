/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.usecase.file

import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.collection.BasicCollection
import com.sorrowblue.comicviewer.domain.model.collection.Collection
import com.sorrowblue.comicviewer.domain.model.collection.CollectionFile
import com.sorrowblue.comicviewer.domain.model.collection.CollectionId
import com.sorrowblue.comicviewer.domain.model.collection.SmartCollection
import com.sorrowblue.comicviewer.domain.model.common.InternalDataApi
import com.sorrowblue.comicviewer.domain.model.common.Resource
import com.sorrowblue.comicviewer.domain.model.file.Book
import com.sorrowblue.comicviewer.domain.model.file.BookFile
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.model.file.Folder
import com.sorrowblue.comicviewer.domain.model.search.SearchCondition
import com.sorrowblue.comicviewer.domain.model.settings.folder.FolderDisplaySettings
import com.sorrowblue.comicviewer.domain.model.settings.folder.FolderScopeOnly
import com.sorrowblue.comicviewer.domain.model.settings.folder.SortType
import com.sorrowblue.comicviewer.domain.model.settings.folder.normalizeFolderPath
import com.sorrowblue.comicviewer.domain.service.book.BookNavigationService
import com.sorrowblue.comicviewer.domain.usecase.StubCollectionFileRepository
import com.sorrowblue.comicviewer.domain.usecase.StubCollectionRepository
import com.sorrowblue.comicviewer.domain.usecase.StubFileRepository
import com.sorrowblue.comicviewer.domain.usecase.StubSettingsRepository
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

        val fakeFileRepository = object : StubFileRepository() {
            override suspend fun findBy(bookshelfId: BookshelfId, path: String): File? = book1

            override fun nextFile(
                bookshelfId: BookshelfId,
                path: String,
                sortType: SortType,
            ): Flow<File?> {
                requestedSortType = sortType
                return flowOf(book2)
            }
        }

        val useCase = GetNextBookUseCaseImpl(
            settingsRepository = fakeSettingsRepository,
            fileRepository = fakeFileRepository,
            collectionFileRepository = StubCollectionFileRepository(),
            collectionRepository = StubCollectionRepository(),
            bookNavigationService = FakeBookNavigationService,
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

        val fakeFileRepository = object : StubFileRepository() {
            override suspend fun findBy(bookshelfId: BookshelfId, path: String): File? = book1
            override fun nextFile(
                bookshelfId: BookshelfId,
                path: String,
                sortType: SortType,
            ): Flow<File?> = flowOf(nonBookFolder)
        }

        val useCase = GetNextBookUseCaseImpl(
            settingsRepository = FakeSettingsRepository(),
            fileRepository = fakeFileRepository,
            collectionFileRepository = StubCollectionFileRepository(),
            collectionRepository = StubCollectionRepository(),
            bookNavigationService = FakeBookNavigationService,
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

        val fakeCollectionRepository = object : StubCollectionRepository() {
            override fun flow(id: CollectionId): Flow<Collection?> = flowOf(basicCollection)
        }

        val fakeCollectionFileRepository = object : StubCollectionFileRepository() {
            override fun flowNextCollectionFile(
                file: CollectionFile,
                sortType: SortType,
            ): Flow<File?> {
                requestedSortType = sortType
                return flowOf(book2)
            }
        }

        val useCase = GetNextBookUseCaseImpl(
            settingsRepository = fakeSettingsRepository,
            fileRepository = StubFileRepository(),
            collectionFileRepository = fakeCollectionFileRepository,
            collectionRepository = fakeCollectionRepository,
            bookNavigationService = FakeBookNavigationService,
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

        val fakeCollectionRepository = object : StubCollectionRepository() {
            override fun flow(id: CollectionId): Flow<Collection?> = flowOf(smartCollection)
        }

        val fakeFileRepository = object : StubFileRepository() {
            override fun nextFile(
                bookshelfId: BookshelfId,
                path: String,
                searchCondition: SearchCondition,
                sortType: SortType,
            ): Flow<File?> {
                requestedSortType = sortType
                return flowOf(book2)
            }
        }

        val useCase = GetNextBookUseCaseImpl(
            settingsRepository = fakeSettingsRepository,
            fileRepository = fakeFileRepository,
            collectionFileRepository = StubCollectionFileRepository(),
            collectionRepository = fakeCollectionRepository,
            bookNavigationService = FakeBookNavigationService,
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

        val fakeCollectionRepository = object : StubCollectionRepository() {
            override fun flow(id: CollectionId): Flow<Collection?> = flowOf(null)
        }

        val useCase = GetNextBookUseCaseImpl(
            settingsRepository = FakeSettingsRepository(),
            fileRepository = StubFileRepository(),
            collectionFileRepository = StubCollectionFileRepository(),
            collectionRepository = fakeCollectionRepository,
            bookNavigationService = FakeBookNavigationService,
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

    @Test
    fun testFolder_whenFindByThrowsException_returnsSystemError() = runTest {
        val fakeFileRepository = object : StubFileRepository() {
            override suspend fun findBy(bookshelfId: BookshelfId, path: String): File? =
                error("DB failure")
        }

        val useCase = GetNextBookUseCaseImpl(
            settingsRepository = FakeSettingsRepository(),
            fileRepository = fakeFileRepository,
            collectionFileRepository = StubCollectionFileRepository(),
            collectionRepository = StubCollectionRepository(),
            bookNavigationService = FakeBookNavigationService,
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
        assertEquals(GetNextBookUseCase.Error.System, result.error)
    }
}

private class FakeSettingsRepository(
    initialFolderDisplaySettings: FolderDisplaySettings = FolderDisplaySettings(),
) : StubSettingsRepository() {
    override val folderDisplaySettings: Flow<FolderDisplaySettings> = flowOf(
        initialFolderDisplaySettings,
    )
}

private object FakeBookNavigationService : BookNavigationService {
    override fun resolveFolderSortType(
        settings: FolderDisplaySettings,
        bookshelfId: BookshelfId,
        parentPath: String,
    ): SortType = settings.currentSortType(bookshelfId, parentPath)

    override fun resolveCollectionSortType(
        collection: Collection,
        settings: FolderDisplaySettings,
    ): SortType = when (collection) {
        is SmartCollection -> collection.searchCondition.sortType
        is BasicCollection -> settings.sortType
    }

    override fun extractParentPath(path: String): String {
        val trimmed = normalizeFolderPath(path).removeSuffix("/")
        val lastIndex = trimmed.lastIndexOf('/')
        return if (lastIndex > 0) trimmed.substring(0, lastIndex) else ""
    }

    override fun validateBook(file: File?): Book? = file as? Book
}
