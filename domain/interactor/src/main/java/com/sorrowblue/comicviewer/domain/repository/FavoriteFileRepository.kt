package com.sorrowblue.comicviewer.domain.repository

import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.sorrowblue.comicviewer.domain.model.favorite.FavoriteFile
import com.sorrowblue.comicviewer.domain.model.favorite.FavoriteId
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.framework.Result
import kotlinx.coroutines.flow.Flow

interface FavoriteFileRepository {

    fun pagingDataFlow(pagingConfig: PagingConfig, favoriteId: FavoriteId): Flow<PagingData<File>>

    suspend fun add(favoriteFile: FavoriteFile)

    suspend fun delete(favoriteFile: FavoriteFile)
    fun getNextRelFile(favoriteFile: FavoriteFile, isNext: Boolean): Flow<Result<File, Unit>>
}
