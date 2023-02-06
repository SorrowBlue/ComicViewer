package com.sorrowblue.comicviewer.data.datasource

import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.sorrowblue.comicviewer.data.common.FileModel
import com.sorrowblue.comicviewer.data.common.bookshelf.BookshelfModelId
import com.sorrowblue.comicviewer.data.common.bookshelf.SortType
import com.sorrowblue.comicviewer.data.common.favorite.FavoriteFileModel
import com.sorrowblue.comicviewer.data.common.favorite.FavoriteModel
import com.sorrowblue.comicviewer.data.common.favorite.FavoriteModelId
import kotlinx.coroutines.flow.Flow

interface FavoriteLocalDataSource {

    suspend fun getFavoriteList(id: BookshelfModelId, filePath: String): List<FavoriteModel>

    suspend fun add(favoriteFileModel: FavoriteFileModel)
    suspend fun remove(favoriteFileModel: FavoriteFileModel)
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
