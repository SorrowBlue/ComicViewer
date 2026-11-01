/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.usecase.collection

import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.collection.BasicCollection
import com.sorrowblue.comicviewer.domain.model.collection.Collection
import com.sorrowblue.comicviewer.domain.model.collection.CollectionCriteria
import com.sorrowblue.comicviewer.domain.model.collection.CollectionFile
import com.sorrowblue.comicviewer.domain.model.collection.CollectionId
import com.sorrowblue.comicviewer.domain.model.collection.CollectionType
import com.sorrowblue.comicviewer.domain.model.common.InternalDataApi
import com.sorrowblue.comicviewer.domain.model.common.Resource
import com.sorrowblue.comicviewer.domain.model.settings.CollectionSettings
import com.sorrowblue.comicviewer.domain.usecase.StubCollectionFileRepository
import com.sorrowblue.comicviewer.domain.usecase.StubCollectionRepository
import com.sorrowblue.comicviewer.domain.usecase.StubSettingsRepository
import kotlin.coroutines.cancellation.CancellationException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest

@OptIn(InternalDataApi::class)
class CollectionUseCasesTest {

    private val collectionId = CollectionId(10)
    private val collection = BasicCollection(name = "Favorites")

    @Test
    fun createCollection_whenSuccess_returnsCreatedCollection() = runTest {
        val created = BasicCollection(name = "Favorites")
        val useCase = CreateCollectionUseCaseImpl(
            repository = object : StubCollectionRepository() {
                override suspend fun create(collection: Collection): Collection = created
            },
        )

        val result = useCase(collection)

        assertEquals(Resource.Success<Collection>(created), result)
    }

    @Test
    fun createCollection_whenRepositoryThrowsException_returnsError() = runTest {
        val useCase = CreateCollectionUseCaseImpl(
            repository = object : StubCollectionRepository() {
                override suspend fun create(collection: Collection): Collection =
                    error("Create failed")
            },
        )

        val result = useCase(collection)

        assertEquals(Resource.Error(Unit), result)
    }

    @Test
    fun createCollection_whenCancelled_rethrowsCancellationException() = runTest {
        val useCase = CreateCollectionUseCaseImpl(
            repository = object : StubCollectionRepository() {
                override suspend fun create(collection: Collection): Collection =
                    throw CancellationException("Cancelled")
            },
        )

        assertFailsWith<CancellationException> {
            useCase(collection)
        }
    }

    @Test
    fun updateCollection_whenSuccess_updatesAndReturnsSuccess() = runTest {
        var updatedCollection: Collection? = null
        val useCase = UpdateCollectionUseCaseImpl(
            repository = object : StubCollectionRepository() {
                override suspend fun update(collection: Collection) {
                    updatedCollection = collection
                }
            },
        )

        val result = useCase(collection)

        assertEquals(Resource.Success(Unit), result)
        assertEquals(collection, updatedCollection)
    }

    @Test
    fun updateCollection_whenRepositoryThrowsException_returnsError() = runTest {
        val useCase = UpdateCollectionUseCaseImpl(
            repository = object : StubCollectionRepository() {
                override suspend fun update(collection: Collection): Unit = error("Update failed")
            },
        )

        val result = useCase(collection)

        assertEquals(Resource.Error(Unit), result)
    }

    @Test
    fun updateCollection_whenCancelled_rethrowsCancellationException() = runTest {
        val useCase = UpdateCollectionUseCaseImpl(
            repository = object : StubCollectionRepository() {
                override suspend fun update(collection: Collection): Unit =
                    throw CancellationException("Cancelled")
            },
        )

        assertFailsWith<CancellationException> {
            useCase(collection)
        }
    }

    @Test
    fun deleteCollection_whenSuccess_deletesAndReturnsSuccess() = runTest {
        var deletedId: CollectionId? = null
        val useCase = DeleteCollectionUseCaseImpl(
            repository = object : StubCollectionRepository() {
                override suspend fun delete(collectionId: CollectionId) {
                    deletedId = collectionId
                }
            },
        )

        val result = useCase(collectionId)

        assertEquals(Resource.Success(Unit), result)
        assertEquals(collectionId, deletedId)
    }

    @Test
    fun deleteCollection_whenRepositoryThrowsException_returnsError() = runTest {
        val useCase = DeleteCollectionUseCaseImpl(
            repository = object : StubCollectionRepository() {
                override suspend fun delete(collectionId: CollectionId): Unit =
                    error("Delete failed")
            },
        )

        val result = useCase(collectionId)

        assertEquals(Resource.Error(Unit), result)
    }

    @Test
    fun deleteCollection_whenCancelled_rethrowsCancellationException() = runTest {
        val useCase = DeleteCollectionUseCaseImpl(
            repository = object : StubCollectionRepository() {
                override suspend fun delete(collectionId: CollectionId): Unit =
                    throw CancellationException("Cancelled")
            },
        )

        assertFailsWith<CancellationException> {
            useCase(collectionId)
        }
    }

    @Test
    fun removeCollectionFile_whenSuccess_removesAndReturnsSuccess() = runTest {
        val collectionFile = CollectionFile(collectionId, BookshelfId(1), "/manga/vol1.cbz")
        var removedFile: CollectionFile? = null
        val useCase = RemoveCollectionFileUseCaseImpl(
            repository = object : StubCollectionFileRepository() {
                override suspend fun remove(file: CollectionFile) {
                    removedFile = file
                }
            },
        )

        val result = useCase(collectionFile)

        assertEquals(Resource.Success(Unit), result)
        assertEquals(collectionFile, removedFile)
    }

    @Test
    fun removeCollectionFile_whenRepositoryThrowsException_returnsError() = runTest {
        val collectionFile = CollectionFile(collectionId, BookshelfId(1), "/manga/vol1.cbz")
        val useCase = RemoveCollectionFileUseCaseImpl(
            repository = object : StubCollectionFileRepository() {
                override suspend fun remove(file: CollectionFile): Unit = error("Remove failed")
            },
        )

        val result = useCase(collectionFile)

        assertEquals(Resource.Error(Unit), result)
    }

    @Test
    fun removeCollectionFile_whenCancelled_rethrowsCancellationException() = runTest {
        val collectionFile = CollectionFile(collectionId, BookshelfId(1), "/manga/vol1.cbz")
        val useCase = RemoveCollectionFileUseCaseImpl(
            repository = object : StubCollectionFileRepository() {
                override suspend fun remove(file: CollectionFile): Unit =
                    throw CancellationException("Cancelled")
            },
        )

        assertFailsWith<CancellationException> {
            useCase(collectionFile)
        }
    }

    @Test
    fun pagingCollection_delegatesToCollectionRepository() = runTest {
        val expectedPagingData = PagingData.empty<Collection>()
        var requestedConfig: PagingConfig? = null

        val useCase = PagingCollectionUseCaseImpl(
            repository = object : StubCollectionRepository() {
                override fun pagingDataFlow(
                    pagingConfig: PagingConfig,
                ): Flow<PagingData<Collection>> {
                    requestedConfig = pagingConfig
                    return flowOf(expectedPagingData)
                }
            },
        )

        val pagingConfig = PagingConfig(pageSize = 20)
        val items = useCase(pagingConfig).toList()

        assertEquals(pagingConfig, requestedConfig)
        assertEquals(listOf(expectedPagingData), items)
    }

    @Test
    fun pagingCollectionExist_combinesSettingsWithCriteriaAndDelegatesToRepository() = runTest {
        val expectedPagingData = PagingData.empty<Pair<Collection, Boolean>>()
        val bookshelfId = BookshelfId(1)
        val path = "/manga/vol1.cbz"
        var capturedCriteria: CollectionCriteria? = null
        var capturedBookshelfId: BookshelfId? = null
        var capturedPath: String? = null

        val useCase = PagingCollectionExistUseCaseImpl(
            collectionRepository = object : StubCollectionRepository() {
                override fun pagingDataFlow(
                    pagingConfig: PagingConfig,
                    bookshelfId: BookshelfId,
                    path: String,
                    collectionCriteria: () -> CollectionCriteria,
                ): Flow<PagingData<Pair<Collection, Boolean>>> {
                    capturedBookshelfId = bookshelfId
                    capturedPath = path
                    capturedCriteria = collectionCriteria()
                    return flowOf(expectedPagingData)
                }
            },
            settingsRepository = object : StubSettingsRepository() {
                override val collectionSettings: Flow<CollectionSettings> =
                    flowOf(CollectionSettings(recent = true))
            },
        )

        val request = PagingCollectionExistUseCase.Request(
            pagingConfig = PagingConfig(pageSize = 20),
            bookshelfId = bookshelfId,
            path = path,
            collectionType = CollectionType.Basic,
        )
        val items = useCase(request).toList()

        assertEquals(listOf(expectedPagingData), items)
        assertEquals(bookshelfId, capturedBookshelfId)
        assertEquals(path, capturedPath)
        assertEquals(
            CollectionCriteria(type = CollectionType.Basic, recent = true),
            capturedCriteria,
        )
    }
}
