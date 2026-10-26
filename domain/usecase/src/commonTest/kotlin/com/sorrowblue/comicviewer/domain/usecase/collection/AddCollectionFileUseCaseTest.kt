/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.usecase.collection

import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.collection.CollectionFile
import com.sorrowblue.comicviewer.domain.model.collection.CollectionId
import com.sorrowblue.comicviewer.domain.model.common.InternalDataApi
import com.sorrowblue.comicviewer.domain.model.common.Resource
import com.sorrowblue.comicviewer.domain.usecase.StubCollectionFileRepository
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
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

        val collectionFileRepository = object : StubCollectionFileRepository() {
            override suspend fun add(file: CollectionFile) {
                addedFiles.add(file)
            }
        }

        val useCase = AddCollectionFileUseCaseImpl(
            collectionFileRepository = collectionFileRepository,
        )

        val result = useCase(target)

        assertTrue(result is Resource.Success)
        assertEquals(listOf(target), addedFiles)
    }
}
