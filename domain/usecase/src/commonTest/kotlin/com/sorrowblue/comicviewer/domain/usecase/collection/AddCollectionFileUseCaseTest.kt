/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.usecase.collection

import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.collection.CollectionFile
import com.sorrowblue.comicviewer.domain.model.collection.CollectionId
import com.sorrowblue.comicviewer.domain.model.common.InternalDataApi
import com.sorrowblue.comicviewer.domain.model.common.Resource
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.model.settings.folder.SortType
import com.sorrowblue.comicviewer.domain.repository.CollectionFileRepository
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.test.runTest

@OptIn(InternalDataApi::class)
class AddCollectionFileUseCaseTest {

    @Test
    fun addCollectionFile_delegatesToCollectionFileRepositoryOnly() = runTest {
        val target = CollectionFile(
            id = CollectionId(10),
            bookshelfId = BookshelfId(1),
            path = "/manga/vol1.cbz",
        )
        val addedFiles = mutableListOf<CollectionFile>()

        val collectionFileRepository = object : CollectionFileRepository {
            override suspend fun add(file: CollectionFile) {
                addedFiles.add(file)
            }

            override suspend fun remove(file: CollectionFile) = TODO()
            override fun pagingDataFlow(
                id: CollectionId,
                pagingConfig: PagingConfig,
                sortType: () -> SortType,
            ): Flow<PagingData<File>> = TODO()

            override suspend fun getCacheKeyList(
                id: CollectionId,
                limit: Int,
            ): List<Pair<BookshelfId, String>> = TODO()

            override fun flowNextCollectionFile(
                file: CollectionFile,
                sortType: SortType,
            ): Flow<File?> = TODO()

            override fun flowPrevCollectionFile(
                file: CollectionFile,
                sortType: SortType,
            ): Flow<File?> = TODO()
        }

        val useCase = AddCollectionFileUseCaseImpl(
            collectionFileRepository = collectionFileRepository,
        )

        val result = useCase(target)

        assertTrue(result is Resource.Success)
        assertEquals(listOf(target), addedFiles)
    }
}
