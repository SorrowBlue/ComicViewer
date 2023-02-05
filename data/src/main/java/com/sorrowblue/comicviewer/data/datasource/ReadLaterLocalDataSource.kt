package com.sorrowblue.comicviewer.data.datasource

import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.sorrowblue.comicviewer.data.common.FileModel
import com.sorrowblue.comicviewer.data.common.ReadLaterModel
import com.sorrowblue.comicviewer.framework.Result
import kotlinx.coroutines.flow.Flow

interface ReadLaterLocalDataSource {

    suspend fun add(model: ReadLaterModel): Result<ReadLaterModel, Unit>
    fun pagingDataFlow(pagingConfig: PagingConfig): Flow<PagingData<FileModel>>
}
