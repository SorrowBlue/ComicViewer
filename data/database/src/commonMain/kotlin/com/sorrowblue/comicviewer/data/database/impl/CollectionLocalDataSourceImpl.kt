package com.sorrowblue.comicviewer.data.database.impl

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.sorrowblue.comicviewer.data.database.dao.CollectionDao
import com.sorrowblue.comicviewer.data.database.dao.insert
import com.sorrowblue.comicviewer.data.database.dao.update
import com.sorrowblue.comicviewer.data.database.entity.collection.CollectionEntity
import com.sorrowblue.comicviewer.data.database.entity.collection.CollectionEntityCount
import com.sorrowblue.comicviewer.data.database.entity.collection.CollectionEntityCountExist
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.collection.Collection
import com.sorrowblue.comicviewer.domain.model.collection.CollectionCriteria
import com.sorrowblue.comicviewer.domain.model.collection.CollectionId
import com.sorrowblue.comicviewer.domain.model.collection.CollectionType
import com.sorrowblue.comicviewer.domain.service.IoDispatcher
import com.sorrowblue.comicviewer.domain.service.datasource.CollectionLocalDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import org.koin.core.annotation.Singleton

@Singleton
internal class CollectionLocalDataSourceImpl(
    private val dao: CollectionDao,
    @param:IoDispatcher private val dispatcher: CoroutineDispatcher,
) : CollectionLocalDataSource {

    override fun pagingDataFlow(pagingConfig: PagingConfig): Flow<PagingData<Collection>> {
        return Pager(pagingConfig) { dao.pagingSource() }.flow.map {
            it.map(CollectionEntityCount::toModel)
        }
    }

    override fun pagingDataFlow(
        pagingConfig: PagingConfig,
        bookshelfId: BookshelfId,
        path: String,
        collectionCriteria: () -> CollectionCriteria,
    ): Flow<PagingData<Pair<Collection, Boolean>>> {
        return Pager(pagingConfig) {
            val criteria = collectionCriteria()
            when (criteria.type) {
                CollectionType.Smart -> TODO()
                CollectionType.Basic ->
                    if (criteria.recent) {
                        dao.pagingSourceBasicRecent(bookshelfId, path)
                    } else {
                        dao.pagingSourceBasic(bookshelfId, path)
                    }

                CollectionType.All ->
                    if (criteria.recent) {
                        dao.pagingSourceRecent(bookshelfId, path)
                    } else {
                        dao.pagingSource(bookshelfId, path)
                    }
            }
        }.flow.map { it.map(CollectionEntityCountExist::toModel) }
    }

    override fun flow(id: CollectionId): Flow<Collection?> {
        return dao.flow(id).map { it?.toModel() }
    }

    override suspend fun create(collection: Collection): Collection {
        return withContext(dispatcher) {
            val rowId = dao.insert(CollectionEntity.fromModel(collection))
            dao.flow(CollectionId(rowId.toInt())).first()!!.toModel()
        }
    }

    override suspend fun update(collection: Collection) {
        withContext(dispatcher) {
            dao.update(CollectionEntity.fromModel(collection))
        }
    }

    override suspend fun delete(collectionId: CollectionId) {
        withContext(dispatcher) {
            dao.delete(collectionId)
        }
    }
}
