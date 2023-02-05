package com.sorrowblue.comicviewer.domain.repository

import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.sorrowblue.comicviewer.domain.entity.favorite.FavoriteId
import com.sorrowblue.comicviewer.domain.entity.file.File
import kotlinx.coroutines.flow.Flow

interface FavoriteBookRepository {

    fun pagingDataFlow(pagingConfig: PagingConfig, favoriteId: FavoriteId): Flow<PagingData<File>>
}
