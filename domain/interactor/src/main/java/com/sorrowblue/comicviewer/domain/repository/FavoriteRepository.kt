package com.sorrowblue.comicviewer.domain.repository

import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.sorrowblue.comicviewer.domain.model.favorite.Favorite
import com.sorrowblue.comicviewer.domain.model.favorite.FavoriteId
import kotlinx.coroutines.flow.Flow

interface FavoriteRepository {
    fun pagingDataFlow(pagingConfig: PagingConfig): Flow<PagingData<Favorite>>
    suspend fun create(title: String)
    fun get(favoriteId: FavoriteId): Flow<Favorite>
    suspend fun update(favorite: Favorite): Favorite
    suspend fun delete(favoriteId: FavoriteId)
}
