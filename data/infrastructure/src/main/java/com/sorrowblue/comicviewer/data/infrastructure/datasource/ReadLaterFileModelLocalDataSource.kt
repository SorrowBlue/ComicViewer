package com.sorrowblue.comicviewer.data.infrastructure.datasource

import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.sorrowblue.comicviewer.data.model.FileModel
import com.sorrowblue.comicviewer.data.model.ReadLaterFileModel
import com.sorrowblue.comicviewer.framework.Result
import kotlinx.coroutines.flow.Flow

interface ReadLaterFileModelLocalDataSource {

    suspend fun add(model: ReadLaterFileModel): Result<ReadLaterFileModel, Unit>
    suspend fun delete(model: ReadLaterFileModel): Result<ReadLaterFileModel, Unit>
    fun pagingDataFlow(pagingConfig: PagingConfig): Flow<PagingData<FileModel>>
    suspend fun deleteAll(): Result<Unit, Unit>
}
