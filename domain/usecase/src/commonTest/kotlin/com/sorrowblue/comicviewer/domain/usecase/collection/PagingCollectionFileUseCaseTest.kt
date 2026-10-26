/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.usecase.collection

import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.sorrowblue.comicviewer.domain.model.collection.BasicCollection
import com.sorrowblue.comicviewer.domain.model.collection.Collection
import com.sorrowblue.comicviewer.domain.model.collection.CollectionId
import com.sorrowblue.comicviewer.domain.model.common.InternalDataApi
import com.sorrowblue.comicviewer.domain.model.file.File
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
}
