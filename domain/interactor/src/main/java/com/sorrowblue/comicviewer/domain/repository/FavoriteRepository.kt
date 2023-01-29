package com.sorrowblue.comicviewer.domain.repository

import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.sorrowblue.comicviewer.domain.entity.Favorite
import com.sorrowblue.comicviewer.domain.entity.FavoriteBook
import com.sorrowblue.comicviewer.domain.entity.FavoriteId
import com.sorrowblue.comicviewer.domain.entity.file.File
import com.sorrowblue.comicviewer.domain.entity.server.ServerId
import kotlinx.coroutines.flow.Flow

interface FavoriteRepository {
    suspend fun getFavoriteList(serverId: ServerId, filePath: String): List<Favorite>
    suspend fun add(favoriteBook: FavoriteBook)
    suspend fun remove(favoriteBook: FavoriteBook)
    fun pagingSourceCount(pagingConfig: PagingConfig): Flow<PagingData<Favorite>>
    suspend fun create(title: String)
    fun get(favoriteId: FavoriteId): Flow<Favorite>
    suspend fun update(favorite: Favorite): Favorite
    suspend fun delete(favoriteId: FavoriteId)
}

interface FavoriteBookRepository {

    fun pagingSource(pagingConfig: PagingConfig, favoriteId: FavoriteId): Flow<PagingData<File>>
}
