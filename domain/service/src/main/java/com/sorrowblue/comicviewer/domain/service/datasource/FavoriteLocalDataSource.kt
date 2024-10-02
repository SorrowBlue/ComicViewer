package com.sorrowblue.comicviewer.domain.service.datasource

import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.sorrowblue.comicviewer.domain.model.Resource
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.favorite.Favorite
import com.sorrowblue.comicviewer.domain.model.favorite.FavoriteId
import kotlinx.coroutines.flow.Flow

interface FavoriteLocalDataSource {

    fun flow(favoriteModelId: FavoriteId): Flow<Favorite>

    fun pagingDataFlow(
        pagingConfig: PagingConfig,
        bookshelfId: BookshelfId,
        path: String,
        isRecent: Boolean,
    ): Flow<PagingData<Favorite>>

    suspend fun create(favoriteModel: Favorite): Resource<Favorite, FavoriteLocalDataSourceError>

    suspend fun update(favoriteModel: Favorite): Favorite

    suspend fun delete(favoriteModelId: FavoriteId)
}

sealed interface FavoriteLocalDataSourceError : Resource.AppError {

    data object NotFound : FavoriteLocalDataSourceError
    data class System(val throwable: Throwable) : FavoriteLocalDataSourceError
}
