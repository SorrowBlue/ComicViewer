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

interface CollectionRepository {
    fun pagingDataFlow(pagingConfig: PagingConfig): Flow<PagingData<Collection>>

    fun pagingDataFlow(
        pagingConfig: PagingConfig,
        bookshelfId: BookshelfId,
        path: String,
        collectionCriteria: () -> CollectionCriteria,
    ): Flow<PagingData<Pair<Collection, Boolean>>>

    fun flow(id: CollectionId): Flow<Collection?>

    suspend fun create(collection: Collection): Collection

    suspend fun update(collection: Collection)

    suspend fun delete(collectionId: CollectionId)
}
