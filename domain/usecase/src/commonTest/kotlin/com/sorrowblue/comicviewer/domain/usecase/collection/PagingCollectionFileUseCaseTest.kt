/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.usecase.collection

import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.collection.BasicCollection
import com.sorrowblue.comicviewer.domain.model.collection.Collection
import com.sorrowblue.comicviewer.domain.model.collection.CollectionId
import com.sorrowblue.comicviewer.domain.model.collection.SmartCollection
import com.sorrowblue.comicviewer.domain.model.common.InternalDataApi
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.model.search.SearchCondition
import com.sorrowblue.comicviewer.domain.model.settings.folder.FolderDisplaySettings
import com.sorrowblue.comicviewer.domain.model.settings.folder.SortType
import com.sorrowblue.comicviewer.domain.usecase.StubCollectionFileRepository
import com.sorrowblue.comicviewer.domain.usecase.StubCollectionRepository
import com.sorrowblue.comicviewer.domain.usecase.StubFileRepository
import com.sorrowblue.comicviewer.domain.usecase.StubSettingsRepository
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDateTime

@OptIn(InternalDataApi::class)
class PagingCollectionFileUseCaseTest {

    private val collectionId = CollectionId(10)

    @Test
    fun pagingCollectionFile_whenCollectionIsNull_returnsEmptyFlow() = runTest {
        val useCase = PagingCollectionFileUseCaseImpl(
            collectionRepository = object : StubCollectionRepository() {
                override fun flow(id: CollectionId): Flow<Collection?> = flowOf(null)
            },
            collectionFileRepository = StubCollectionFileRepository(),
            settingsRepository = StubSettingsRepository(),
            fileRepository = StubFileRepository(),
        )

        val items = useCase(
            PagingCollectionFileUseCase.Request(collectionId, PagingConfig(20)),
        ).toList()

        assertTrue(items.isEmpty())
    }

    @Test
    fun pagingCollectionFile_whenBasicCollection_delegatesToCollectionFileRepository() = runTest {
        val expectedPagingData = PagingData.empty<File>()
        val useCase = PagingCollectionFileUseCaseImpl(
            collectionRepository = object : StubCollectionRepository() {
                override fun flow(id: CollectionId): Flow<Collection?> =
                    flowOf(BasicCollection("Favorites"))
            },
            collectionFileRepository = object : StubCollectionFileRepository() {
                override fun pagingDataFlow(
                    id: CollectionId,
                    pagingConfig: PagingConfig,
                    sortType: () -> SortType,
                ): Flow<PagingData<File>> = flowOf(expectedPagingData)
            },
            settingsRepository = object : StubSettingsRepository() {
                override val folderDisplaySettings: Flow<FolderDisplaySettings> =
                    flowOf(FolderDisplaySettings())
            },
            fileRepository = StubFileRepository(),
        )

        val items = useCase(
            PagingCollectionFileUseCase.Request(collectionId, PagingConfig(20)),
        ).toList()

        assertEquals(listOf(expectedPagingData), items)
    }

    @Test
    fun pagingCollectionFile_whenCountOrNameChanges_doesNotRecreatePager() = runTest {
        val timestamp = LocalDateTime(2026, 1, 1, 0, 0)
        val initialBasic = BasicCollection(
            id = collectionId,
            name = "Favorites",
            count = 1,
            createdAt = timestamp,
            updatedAt = timestamp,
        )
        val updatedBasicCountAndName = BasicCollection(
            id = collectionId,
            name = "Favorites Renamed",
            count = 2,
            createdAt = timestamp,
            updatedAt = LocalDateTime(2026, 1, 2, 0, 0),
        )
        var basicPagerCreationCount = 0

        val basicUseCase = PagingCollectionFileUseCaseImpl(
            collectionRepository = object : StubCollectionRepository() {
                override fun flow(id: CollectionId): Flow<Collection?> =
                    flowOf(initialBasic, updatedBasicCountAndName)
            },
            collectionFileRepository = object : StubCollectionFileRepository() {
                override fun pagingDataFlow(
                    id: CollectionId,
                    pagingConfig: PagingConfig,
                    sortType: () -> SortType,
                ): Flow<PagingData<File>> {
                    basicPagerCreationCount++
                    return flowOf(PagingData.empty())
                }
            },
            settingsRepository = object : StubSettingsRepository() {
                override val folderDisplaySettings: Flow<FolderDisplaySettings> =
                    flowOf(FolderDisplaySettings())
            },
            fileRepository = StubFileRepository(),
        )

        basicUseCase(
            PagingCollectionFileUseCase.Request(collectionId, PagingConfig(pageSize = 20)),
        ).toList()
        assertEquals(1, basicPagerCreationCount)

        val condition = SearchCondition(query = "naruto")
        val initialSmart = SmartCollection(
            id = collectionId,
            name = "Smart",
            count = 5,
            createdAt = timestamp,
            updatedAt = timestamp,
            bookshelfId = BookshelfId(1),
            searchCondition = condition,
        )
        val updatedSmartCountAndName = SmartCollection(
            id = collectionId,
            name = "Smart Renamed",
            count = 10,
            createdAt = timestamp,
            updatedAt = LocalDateTime(2026, 1, 2, 0, 0),
            bookshelfId = BookshelfId(1),
            searchCondition = condition,
        )
        var smartPagerCreationCount = 0

        val smartUseCase = PagingCollectionFileUseCaseImpl(
            collectionRepository = object : StubCollectionRepository() {
                override fun flow(id: CollectionId): Flow<Collection?> =
                    flowOf(initialSmart, updatedSmartCountAndName)
            },
            collectionFileRepository = StubCollectionFileRepository(),
            settingsRepository = StubSettingsRepository(),
            fileRepository = object : StubFileRepository() {
                override fun pagingDataFlow(
                    pagingConfig: PagingConfig,
                    bookshelfId: BookshelfId?,
                    searchCondition: () -> SearchCondition,
                ): Flow<PagingData<File>> {
                    smartPagerCreationCount++
                    return flowOf(PagingData.empty())
                }
            },
        )

        smartUseCase(
            PagingCollectionFileUseCase.Request(collectionId, PagingConfig(pageSize = 20)),
        ).toList()
        assertEquals(1, smartPagerCreationCount)
    }
}
