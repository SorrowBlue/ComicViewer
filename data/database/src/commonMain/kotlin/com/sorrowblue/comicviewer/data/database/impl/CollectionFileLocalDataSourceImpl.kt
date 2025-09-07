package com.sorrowblue.comicviewer.data.database.impl

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.sorrowblue.comicviewer.data.database.dao.CollectionDao
import com.sorrowblue.comicviewer.data.database.dao.CollectionFileDao
import com.sorrowblue.comicviewer.data.database.dao.FileDao
import com.sorrowblue.comicviewer.data.database.dao.bookshelfIdCacheKey
import com.sorrowblue.comicviewer.data.database.dao.pagingSource
import com.sorrowblue.comicviewer.data.database.entity.collection.CollectionFileEntity
import com.sorrowblue.comicviewer.data.database.entity.file.FileEntity
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.collection.BasicCollection
import com.sorrowblue.comicviewer.domain.model.collection.CollectionFile
import com.sorrowblue.comicviewer.domain.model.collection.CollectionId
import com.sorrowblue.comicviewer.domain.model.collection.SmartCollection
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.model.settings.folder.SortType
import com.sorrowblue.comicviewer.domain.service.IoDispatcher
import com.sorrowblue.comicviewer.domain.service.datasource.CollectionFileLocalDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import org.koin.core.annotation.Single

@Single
internal class CollectionFileLocalDataSourceImpl(
    private val dao: CollectionDao,
    private val collectionFileDao: CollectionFileDao,
    private val fileDao: FileDao,
    @param:IoDispatcher private val dispatcher: CoroutineDispatcher,
) : CollectionFileLocalDataSource {

    override fun pagingDataFlow(
        id: CollectionId,
        pagingConfig: PagingConfig,
        sortType: () -> SortType,
    ): Flow<PagingData<File>> {
        return Pager(pagingConfig) {
            collectionFileDao.pagingSource(id.value, sortType())
        }.flow.map { it.map(FileEntity::toModel) }
    }

    override suspend fun add(file: CollectionFile) {
        withContext(dispatcher) {
            collectionFileDao.insert(CollectionFileEntity.fromModel(file))
        }
    }

    override suspend fun remove(file: CollectionFile) {
        withContext(dispatcher) {
            collectionFileDao.delete(CollectionFileEntity.fromModel(file))
        }
    }

    override suspend fun getCacheKeyList(
        id: CollectionId,
        limit: Int,
    ): List<Pair<BookshelfId, String>> {
        return when (val collection = dao.flow(id).first()!!.toModel()) {
            is BasicCollection -> collectionFileDao.findBasicCollectionFileCacheKey(
                id,
                limit
            ).map { BookshelfId(it.bookshelfId) to it.cacheKey }

            is SmartCollection -> fileDao.bookshelfIdCacheKey(
                collection.bookshelfId,
                collection.searchCondition,
                limit
            ).map { BookshelfId(it.bookshelfId) to it.cacheKey }
        }
    }

    override fun flowNextCollectionFile(file: CollectionFile, sortType: SortType): Flow<File?> {
        TODO("Not yet implemented")
    }

    override fun flowPrevCollectionFile(file: CollectionFile, sortType: SortType): Flow<File?> {
        TODO("Not yet implemented")
    }
}
