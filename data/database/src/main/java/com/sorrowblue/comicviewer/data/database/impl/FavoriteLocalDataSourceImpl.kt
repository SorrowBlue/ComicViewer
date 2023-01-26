package com.sorrowblue.comicviewer.data.database.impl

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.sorrowblue.comicviewer.data.common.FavoriteBookModel
import com.sorrowblue.comicviewer.data.common.FavoriteModel
import com.sorrowblue.comicviewer.data.common.FavoriteModelId
import com.sorrowblue.comicviewer.data.common.FileModel
import com.sorrowblue.comicviewer.data.common.ServerModelId
import com.sorrowblue.comicviewer.data.common.SortType
import com.sorrowblue.comicviewer.data.database.dao.FavoriteBookDao
import com.sorrowblue.comicviewer.data.database.dao.FavoriteDao
import com.sorrowblue.comicviewer.data.database.dao.pagingSource
import com.sorrowblue.comicviewer.data.database.entity.Favorite
import com.sorrowblue.comicviewer.data.database.entity.File
import com.sorrowblue.comicviewer.data.database.entity.toFavorite
import com.sorrowblue.comicviewer.data.database.entity.toFavoriteBook
import com.sorrowblue.comicviewer.data.datasource.FavoriteBookLocalDataSource
import com.sorrowblue.comicviewer.data.datasource.FavoriteLocalDataSource
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import logcat.logcat

internal class FavoriteBookLocalDataSourceImpl @Inject constructor(
    private val favoriteBookDao: FavoriteBookDao,
) : FavoriteBookLocalDataSource {
    override fun pagingSource(
        pagingConfig: PagingConfig,
        favoriteModelId: FavoriteModelId,
        sortType: () -> SortType
    ): Flow<PagingData<FileModel>> {
        return Pager(pagingConfig) {
            favoriteBookDao.pagingSource(favoriteModelId.value, sortType.invoke())
        }.flow.map { it.map(File::toFileModel) }
    }

    override suspend fun getCacheKeyList(favoriteModelId: FavoriteModelId, limit: Int): List<String> {
        return favoriteBookDao.selectCacheKey(favoriteModelId.value, limit).also {
            logcat { "cacheKeyList=${it}" }
        }
    }
}

internal class FavoriteLocalDataSourceImpl @Inject constructor(
    private val favoriteDao: FavoriteDao,
    private val favoriteBookDao: FavoriteBookDao,
) : FavoriteLocalDataSource {

    override suspend fun get(favoriteModelId: FavoriteModelId): FavoriteModel {
        return favoriteDao.findBy(favoriteModelId.value)!!.let {
            FavoriteModel(FavoriteModelId(it.favorite.id), it.favorite.name, it.count)
        }
    }

    override suspend fun update(favoriteModel: FavoriteModel): FavoriteModel {
        favoriteDao.upsert(favoriteModel.toFavorite())
        return favoriteModel
    }

    override suspend fun delete(favoriteModelId: FavoriteModelId) {
        return favoriteDao.delete(Favorite(favoriteModelId.value, ""))
    }

    override suspend fun getFavoriteList(id: ServerModelId, filePath: String): List<FavoriteModel> {
        return favoriteDao.selectBy(id.value, filePath).map(Favorite::toModel)
    }

    override suspend fun add(favoriteBookModel: FavoriteBookModel) {
        favoriteBookDao.insert(favoriteBookModel.toFavoriteBook())
    }

    override suspend fun remove(favoriteBookModel: FavoriteBookModel) {
        favoriteBookDao.delete(favoriteBookModel.toFavoriteBook())
    }

    override fun pagingSourceCount(pagingConfig: PagingConfig): Flow<PagingData<FavoriteModel>> {
        return Pager(pagingConfig) {
            favoriteDao.pagingSourceCount()
        }.flow.map {
            it.map {
                FavoriteModel(FavoriteModelId(it.favorite.id), it.favorite.name, it.count)
            }
        }
    }

    override suspend fun create(favoriteModel: FavoriteModel) {
        logcat { "create=$favoriteModel" }
        favoriteDao.upsert(favoriteModel.toFavorite())
    }
}
