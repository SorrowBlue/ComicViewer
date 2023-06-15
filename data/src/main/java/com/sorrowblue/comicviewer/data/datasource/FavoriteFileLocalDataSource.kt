package com.sorrowblue.comicviewer.data.datasource

import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.sorrowblue.comicviewer.data.common.FileModel
import com.sorrowblue.comicviewer.data.common.bookshelf.SortEntity
import com.sorrowblue.comicviewer.data.common.favorite.FavoriteFileModel
import com.sorrowblue.comicviewer.data.common.favorite.FavoriteModelId
import kotlinx.coroutines.flow.Flow

interface FavoriteFileLocalDataSource {

    suspend fun add(favoriteFileModel: FavoriteFileModel)

    suspend fun delete(favoriteFileModel: FavoriteFileModel)

    fun pagingSource(
        pagingConfig: PagingConfig,
        favoriteModelId: FavoriteModelId,
        sortType: () -> SortEntity
    ): Flow<PagingData<FileModel>>

    suspend fun getCacheKeyList(favoriteModelId: FavoriteModelId, limit: Int): List<String>

    fun flowNextFavoriteFile(
        favoriteFileModel: FavoriteFileModel,
        sortEntity: SortEntity
    ): Flow<FileModel?>

    fun flowPrevFavoriteFile(
        favoriteFileModel: FavoriteFileModel,
        sortEntity: SortEntity
    ): Flow<FileModel?>
}
