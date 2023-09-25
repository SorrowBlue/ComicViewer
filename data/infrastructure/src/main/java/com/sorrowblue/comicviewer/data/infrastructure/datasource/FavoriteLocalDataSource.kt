package com.sorrowblue.comicviewer.data.infrastructure.datasource

import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.sorrowblue.comicviewer.data.model.favorite.FavoriteModel
import com.sorrowblue.comicviewer.data.model.favorite.FavoriteModelId
import kotlinx.coroutines.flow.Flow

interface FavoriteLocalDataSource {

    fun flow(favoriteModelId: FavoriteModelId): Flow<FavoriteModel>

    fun pagingDataFlow(pagingConfig: PagingConfig): Flow<PagingData<FavoriteModel>>

    suspend fun create(favoriteModel: FavoriteModel)

    suspend fun update(favoriteModel: FavoriteModel): FavoriteModel

    suspend fun delete(favoriteModelId: FavoriteModelId)
}
