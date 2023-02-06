package com.sorrowblue.comicviewer.data.datasource

import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.sorrowblue.comicviewer.data.common.FileModel
import com.sorrowblue.comicviewer.data.common.ReadLaterFileModel
import com.sorrowblue.comicviewer.framework.Result
import kotlinx.coroutines.flow.Flow

interface ReadLaterFileModelLocalDataSource {

    suspend fun add(model: ReadLaterFileModel): Result<ReadLaterFileModel, Unit>
    fun pagingDataFlow(pagingConfig: PagingConfig): Flow<PagingData<FileModel>>
}
