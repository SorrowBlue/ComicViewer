package com.sorrowblue.comicviewer.data.datasource

import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.sorrowblue.comicviewer.data.common.FavoriteBookModel
import com.sorrowblue.comicviewer.data.common.FavoriteModel
import com.sorrowblue.comicviewer.data.common.FavoriteModelId
import com.sorrowblue.comicviewer.data.common.FileModel
import com.sorrowblue.comicviewer.data.common.ServerModelId
import com.sorrowblue.comicviewer.data.common.SortType
import kotlinx.coroutines.flow.Flow

interface FavoriteLocalDataSource {

    suspend fun getFavoriteList(id: ServerModelId, filePath: String): List<FavoriteModel>

    suspend fun add(favoriteBookModel: FavoriteBookModel)
    suspend fun remove(favoriteBookModel: FavoriteBookModel)
    fun pagingSourceCount(pagingConfig: PagingConfig): Flow<PagingData<FavoriteModel>>
    suspend fun create(favoriteModel: FavoriteModel)
    fun get(favoriteModelId: FavoriteModelId): Flow<FavoriteModel>
    suspend fun delete(favoriteModelId: FavoriteModelId)
    suspend fun update(favoriteModel: FavoriteModel): FavoriteModel
}

interface FavoriteBookLocalDataSource {

    fun pagingSource(
        pagingConfig: PagingConfig,
        favoriteModelId: FavoriteModelId,
        sortType: () -> SortType
    ): Flow<PagingData<FileModel>>

    suspend fun getCacheKeyList(favoriteModelId: FavoriteModelId, limit: Int): List<String>
}
