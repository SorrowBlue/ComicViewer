package com.sorrowblue.comicviewer.domain.repository

import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.sorrowblue.comicviewer.domain.entity.favorite.Favorite
import com.sorrowblue.comicviewer.domain.entity.favorite.FavoriteFile
import com.sorrowblue.comicviewer.domain.entity.favorite.FavoriteId
import com.sorrowblue.comicviewer.domain.entity.server.BookshelfId
import kotlinx.coroutines.flow.Flow

interface FavoriteRepository {
    suspend fun getFavoriteList(bookshelfId: BookshelfId, filePath: String): List<Favorite>
    suspend fun add(favoriteFile: FavoriteFile)
    suspend fun remove(favoriteFile: FavoriteFile)
    fun pagingDataFlow(pagingConfig: PagingConfig): Flow<PagingData<Favorite>>
    suspend fun create(title: String)
    fun get(favoriteId: FavoriteId): Flow<Favorite>
    suspend fun update(favorite: Favorite): Favorite
    suspend fun delete(favoriteId: FavoriteId)
}
