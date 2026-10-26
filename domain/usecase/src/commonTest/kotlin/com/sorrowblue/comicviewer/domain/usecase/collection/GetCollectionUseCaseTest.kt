/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.usecase.collection

import com.sorrowblue.comicviewer.domain.model.collection.BasicCollection
import com.sorrowblue.comicviewer.domain.model.collection.Collection
import com.sorrowblue.comicviewer.domain.model.collection.CollectionId
import com.sorrowblue.comicviewer.domain.model.common.InternalDataApi
import com.sorrowblue.comicviewer.domain.model.common.Resource
import com.sorrowblue.comicviewer.domain.usecase.StubCollectionRepository
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest

@OptIn(InternalDataApi::class)
class GetCollectionUseCaseTest {

    private val collectionId = CollectionId(10)
    private val collection = BasicCollection(name = "Favorites")

    @Test
    fun getCollection_whenExists_emitsSuccess() = runTest {
        val useCase = GetCollectionUseCaseImpl(
            repository = object : StubCollectionRepository() {
                override fun flow(id: CollectionId): Flow<Collection?> = flowOf(collection)
            },
        )

        val result = useCase(collectionId).first()
        assertEquals(Resource.Success(collection), result)
    }

    @Test
    fun getCollection_whenNull_emitsNotFoundError() = runTest {
        val useCase = GetCollectionUseCaseImpl(
            repository = object : StubCollectionRepository() {
                override fun flow(id: CollectionId): Flow<Collection?> = flowOf(null)
            },
        )

        val result = useCase(collectionId).first()
        assertEquals(Resource.Error(GetCollectionUseCase.Error.NotFound), result)
    }

    @Test
    fun getCollection_whenFlowThrowsException_emitsSystemError() = runTest {
        val useCase = GetCollectionUseCaseImpl(
            repository = object : StubCollectionRepository() {
                override fun flow(id: CollectionId): Flow<Collection?> = flow {
                    throw IllegalStateException("DB failure")
                }
            },
        )

        val result = useCase(collectionId).first()
        assertEquals(Resource.Error(GetCollectionUseCase.Error.System), result)
    }
}
