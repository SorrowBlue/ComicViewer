package com.sorrowblue.comicviewer.data.database.impl

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.sorrowblue.comicviewer.data.common.favorite.FavoriteModel
import com.sorrowblue.comicviewer.data.common.favorite.FavoriteModelId
import com.sorrowblue.comicviewer.data.database.dao.FavoriteDao
import com.sorrowblue.comicviewer.data.database.entity.Favorite
import com.sorrowblue.comicviewer.data.database.entity.FavoriteFileCount
import com.sorrowblue.comicviewer.data.datasource.FavoriteLocalDataSource
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map

internal class FavoriteLocalDataSourceImpl @Inject constructor(
    private val favoriteDao: FavoriteDao
) : FavoriteLocalDataSource {

    override fun flow(favoriteModelId: FavoriteModelId): Flow<FavoriteModel> {
        return favoriteDao.flow(favoriteModelId.value).filterNotNull()
            .map(FavoriteFileCount::toModel)
    }

    override suspend fun update(favoriteModel: FavoriteModel): FavoriteModel {
        favoriteDao.upsert(Favorite.fromModel(favoriteModel))
        return favoriteModel
    }

    override suspend fun delete(favoriteModelId: FavoriteModelId) {
        favoriteDao.delete(Favorite(favoriteModelId.value, ""))
    }

    override fun pagingDataFlow(pagingConfig: PagingConfig): Flow<PagingData<FavoriteModel>> {
        return Pager(pagingConfig) {
            favoriteDao.pagingSource()
        }.flow.map { pagingData ->
            pagingData.map(FavoriteFileCount::toModel)
        }
    }

    override suspend fun create(favoriteModel: FavoriteModel) {
        favoriteDao.upsert(Favorite.fromModel(favoriteModel))
    }
}
