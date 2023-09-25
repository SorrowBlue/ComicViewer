package com.sorrowblue.comicviewer.data.infrastructure.repository.impl

import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.sorrowblue.comicviewer.data.infrastructure.datasource.ReadLaterFileModelLocalDataSource
import com.sorrowblue.comicviewer.domain.model.ReadLaterFile
import com.sorrowblue.comicviewer.domain.model.Result
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.service.repository.ReadLaterRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

internal class ReadLaterRepositoryImpl @Inject constructor(
    private val readLaterFileModelLocalDataSource: ReadLaterFileModelLocalDataSource,
) : ReadLaterRepository {
    override suspend fun add(readLaterFile: ReadLaterFile): Result<ReadLaterFile, Unit> {
        return readLaterFileModelLocalDataSource.add(readLaterFile).fold({
            Result.Success(it)
        }, {
            Result.Error(it)
        }, {
            Result.Exception(it)
        })
    }

    override fun pagingDataFlow(pagingConfig: PagingConfig): Flow<PagingData<File>> {
        return readLaterFileModelLocalDataSource.pagingDataFlow(pagingConfig)
    }
}
