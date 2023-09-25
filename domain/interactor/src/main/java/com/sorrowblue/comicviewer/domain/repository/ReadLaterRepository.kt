package com.sorrowblue.comicviewer.domain.repository

import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.sorrowblue.comicviewer.domain.model.ReadLaterFile
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.framework.Result
import kotlinx.coroutines.flow.Flow

interface ReadLaterRepository {
    suspend fun add(readLaterFile: ReadLaterFile): Result<ReadLaterFile, Unit>

    fun pagingDataFlow(pagingConfig: PagingConfig): Flow<PagingData<File>>
}
