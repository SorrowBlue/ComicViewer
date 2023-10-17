package com.sorrowblue.comicviewer.domain.service.repository

import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.sorrowblue.comicviewer.domain.model.ReadLaterFile
import com.sorrowblue.comicviewer.domain.model.Result
import com.sorrowblue.comicviewer.domain.model.file.File
import kotlinx.coroutines.flow.Flow

interface ReadLaterRepository {
    suspend fun add(readLaterFile: ReadLaterFile): Result<ReadLaterFile, Unit>

    fun pagingDataFlow(pagingConfig: PagingConfig): Flow<PagingData<File>>
}
