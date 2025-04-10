package com.sorrowblue.comicviewer.domain.service.datasource

import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.collection.Collection
import com.sorrowblue.comicviewer.domain.model.collection.CollectionId
import kotlinx.coroutines.flow.Flow

interface CollectionLocalDataSource {

    fun pagingDataFlow(pagingConfig: PagingConfig): Flow<PagingData<Collection>>

    fun pagingDataFlow(
        pagingConfig: PagingConfig,
        bookshelfId: BookshelfId,
        path: String,
        isRecent: Boolean,
    ): Flow<PagingData<Pair<Collection, Boolean>>>

    fun flow(id: CollectionId): Flow<Collection?>

    suspend fun create(collection: Collection): Collection

    suspend fun update(collection: Collection)

    suspend fun delete(collectionId: CollectionId)
}
