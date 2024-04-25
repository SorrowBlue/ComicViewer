package com.sorrowblue.comicviewer.domain.service.datasource

import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.sorrowblue.comicviewer.domain.model.ReadLaterFile
import com.sorrowblue.comicviewer.domain.model.Result
import com.sorrowblue.comicviewer.domain.model.file.File
import kotlinx.coroutines.flow.Flow

interface ReadLaterFileModelLocalDataSource {

    suspend fun add(model: ReadLaterFile): Result<ReadLaterFile, Unit>
    fun exists(model: ReadLaterFile): Flow<Boolean>
    suspend fun delete(model: ReadLaterFile): Result<ReadLaterFile, Unit>
    fun pagingDataFlow(pagingConfig: PagingConfig): Flow<PagingData<File>>
    suspend fun deleteAll(): Result<Unit, Unit>
}
