package com.sorrowblue.comicviewer.data.database.impl

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.sorrowblue.comicviewer.data.database.dao.FavoriteDao
import com.sorrowblue.comicviewer.data.database.entity.FavoriteEntity
import com.sorrowblue.comicviewer.data.database.entity.FavoriteFileCountEntity
import com.sorrowblue.comicviewer.domain.model.Resource
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.favorite.Favorite
import com.sorrowblue.comicviewer.domain.model.favorite.FavoriteId
import com.sorrowblue.comicviewer.domain.service.datasource.FavoriteLocalDataSource
import com.sorrowblue.comicviewer.domain.service.datasource.FavoriteLocalDataSourceError
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

internal class FavoriteLocalDataSourceImpl @Inject constructor(
    private val favoriteDao: FavoriteDao,
) : FavoriteLocalDataSource {

    override fun flow(favoriteModelId: FavoriteId): Flow<Favorite> {
        return favoriteDao.flow(favoriteModelId.value).filterNotNull()
            .map(FavoriteFileCountEntity::toModel)
    }

    override suspend fun update(favoriteModel: Favorite): Favorite {
        favoriteDao.upsert(FavoriteEntity.fromModel(favoriteModel))
        return favoriteModel
    }

    override suspend fun delete(favoriteModelId: FavoriteId) {
        favoriteDao.delete(FavoriteEntity(favoriteModelId, ""))
    }

    override fun pagingDataFlow(
        pagingConfig: PagingConfig,
        bookshelfId: BookshelfId,
        path: String,
    ): Flow<PagingData<Favorite>> {
        return Pager(pagingConfig) {
            favoriteDao.pagingSource(bookshelfId.value, path)
        }.flow.map { pagingData ->
            pagingData.map(FavoriteFileCountEntity::toModel)
        }
    }

    override suspend fun create(favoriteModel: Favorite): Resource<Favorite, FavoriteLocalDataSourceError> {
        return kotlin.runCatching {
            favoriteDao.upsert(FavoriteEntity.fromModel(favoriteModel))
        }.fold(
            { data ->
                favoriteDao.flow(data.toInt()).first()?.toModel()?.let {
                    Resource.Success(it)
                } ?: Resource.Error(FavoriteLocalDataSourceError.NotFound)
            },
            {
                Resource.Error(FavoriteLocalDataSourceError.System(it))
            }
        )
    }
}
