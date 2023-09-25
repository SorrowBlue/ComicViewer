package com.sorrowblue.comicviewer.data.infrastructure.datasource

import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.sorrowblue.comicviewer.domain.model.favorite.FavoriteFile
import com.sorrowblue.comicviewer.domain.model.favorite.FavoriteId
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.model.settings.SortType
import kotlinx.coroutines.flow.Flow

interface FavoriteFileLocalDataSource {

    suspend fun add(favoriteFileModel: FavoriteFile)

    suspend fun delete(favoriteFileModel: FavoriteFile)

    fun pagingSource(
        pagingConfig: PagingConfig,
        favoriteModelId: FavoriteId,
        sortType: () -> SortType,
    ): Flow<PagingData<File>>

    suspend fun getCacheKeyList(favoriteModelId: FavoriteId, limit: Int): List<String>

    fun flowNextFavoriteFile(favoriteFileModel: FavoriteFile, sortEntity: SortType): Flow<File?>

    fun flowPrevFavoriteFile(favoriteFileModel: FavoriteFile, sortEntity: SortType): Flow<File?>
}
