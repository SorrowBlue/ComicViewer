package com.sorrowblue.comicviewer.data.database.impl

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.sorrowblue.comicviewer.data.common.FileModel
import com.sorrowblue.comicviewer.data.common.bookshelf.BookshelfModelId
import com.sorrowblue.comicviewer.data.common.bookshelf.SortType
import com.sorrowblue.comicviewer.data.common.favorite.FavoriteFileModel
import com.sorrowblue.comicviewer.data.common.favorite.FavoriteModel
import com.sorrowblue.comicviewer.data.common.favorite.FavoriteModelId
import com.sorrowblue.comicviewer.data.database.dao.FavoriteDao
import com.sorrowblue.comicviewer.data.database.dao.FavoriteFileDao
import com.sorrowblue.comicviewer.data.database.entity.Favorite
import com.sorrowblue.comicviewer.data.database.entity.FavoriteFile
import com.sorrowblue.comicviewer.data.database.entity.File
import com.sorrowblue.comicviewer.data.datasource.FavoriteBookLocalDataSource
import com.sorrowblue.comicviewer.data.datasource.FavoriteLocalDataSource
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import logcat.logcat

internal class FavoriteBookLocalDataSourceImpl @Inject constructor(
    private val favoriteFileDao: FavoriteFileDao,
) : FavoriteBookLocalDataSource {
    override fun pagingSource(
        pagingConfig: PagingConfig,
        favoriteModelId: FavoriteModelId,
        sortType: () -> SortType
    ): Flow<PagingData<FileModel>> {
        return Pager(pagingConfig) {
            favoriteFileDao.pagingSource(favoriteModelId.value, sortType.invoke())
        }.flow.map { it.map(File::toModel) }
    }

    override suspend fun getCacheKeyList(favoriteModelId: FavoriteModelId, limit: Int): List<String> {
        return favoriteFileDao.selectCacheKey(favoriteModelId.value, limit).also {
            logcat { "cacheKeyList=${it}" }
        }
    }
}

internal class FavoriteLocalDataSourceImpl @Inject constructor(
    private val favoriteDao: FavoriteDao,
    private val favoriteFileDao: FavoriteFileDao,
) : FavoriteLocalDataSource {

    override fun get(favoriteModelId: FavoriteModelId): Flow<FavoriteModel> {
        return favoriteDao.findByAsFlow(favoriteModelId.value).filterNotNull().map {
            FavoriteModel(FavoriteModelId(it.favorite.id), it.favorite.name, it.count)
        }
    }

    override suspend fun update(favoriteModel: FavoriteModel): FavoriteModel {
        favoriteDao.upsert(Favorite.fromModel(favoriteModel))
        return favoriteModel
    }

    override suspend fun delete(favoriteModelId: FavoriteModelId) {
        return favoriteDao.delete(Favorite(favoriteModelId.value, ""))
    }

    override suspend fun getFavoriteList(id: BookshelfModelId, filePath: String): List<FavoriteModel> {
        return favoriteDao.selectBy(id.value, filePath).map(Favorite::toModel)
    }

    override suspend fun add(favoriteFileModel: FavoriteFileModel) {
        favoriteFileDao.insert(FavoriteFile.fromModel(favoriteFileModel))
    }

    override suspend fun remove(favoriteFileModel: FavoriteFileModel) {
        favoriteFileDao.delete(FavoriteFile.fromModel(favoriteFileModel))
    }

    override fun pagingSourceCount(pagingConfig: PagingConfig): Flow<PagingData<FavoriteModel>> {
        return Pager(pagingConfig) {
            favoriteDao.pagingSourceCount()
        }.flow.map { pagingData ->
            pagingData.map {
                FavoriteModel(FavoriteModelId(it.favorite.id), it.favorite.name, it.count)
            }
        }
    }

    override suspend fun create(favoriteModel: FavoriteModel) {
        favoriteDao.upsert(Favorite.fromModel(favoriteModel))
    }
}
