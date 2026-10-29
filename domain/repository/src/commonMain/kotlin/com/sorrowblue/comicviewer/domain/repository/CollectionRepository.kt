/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.repository

import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.collection.Collection
import com.sorrowblue.comicviewer.domain.model.collection.CollectionCriteria
import com.sorrowblue.comicviewer.domain.model.collection.CollectionId
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for managing [Collection] entities.
 */
interface CollectionRepository {
    /**
     * Observes paginated [Collection] items.
     */
    fun pagingDataFlow(pagingConfig: PagingConfig): Flow<PagingData<Collection>>

    /**
     * Observes paginated [Collection] items along with whether the file at [path] in [bookshelfId]
     * belongs to each collection, filtered by [collectionCriteria].
     */
    fun pagingDataFlow(
        pagingConfig: PagingConfig,
        bookshelfId: BookshelfId,
        path: String,
        collectionCriteria: () -> CollectionCriteria,
    ): Flow<PagingData<Pair<Collection, Boolean>>>

    /**
     * Observes a single [Collection] by its [id].
     */
    fun flow(id: CollectionId): Flow<Collection?>

    /**
     * Creates a new [collection] and returns the persisted [Collection].
     */
    suspend fun create(collection: Collection): Collection

    /**
     * Updates an existing [collection].
     */
    suspend fun update(collection: Collection)

    /**
     * Deletes the collection identified by [collectionId].
     */
    suspend fun delete(collectionId: CollectionId)
}
