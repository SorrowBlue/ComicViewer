package com.sorrowblue.comicviewer.data.infrastructure.datasource

import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.sorrowblue.comicviewer.domain.model.favorite.Favorite
import com.sorrowblue.comicviewer.domain.model.favorite.FavoriteId
import kotlinx.coroutines.flow.Flow

interface FavoriteLocalDataSource {

    fun flow(favoriteModelId: FavoriteId): Flow<Favorite>

    fun pagingDataFlow(pagingConfig: PagingConfig): Flow<PagingData<Favorite>>

    suspend fun create(favoriteModel: Favorite)

    suspend fun update(favoriteModel: Favorite): Favorite

    suspend fun delete(favoriteModelId: FavoriteId)
}
