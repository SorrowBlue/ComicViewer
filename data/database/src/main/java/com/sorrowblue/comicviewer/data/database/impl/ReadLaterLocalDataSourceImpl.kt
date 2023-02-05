package com.sorrowblue.comicviewer.data.database.impl

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.sorrowblue.comicviewer.data.common.FileModel
import com.sorrowblue.comicviewer.data.common.ReadLaterModel
import com.sorrowblue.comicviewer.data.database.dao.ReadLaterDao
import com.sorrowblue.comicviewer.data.database.entity.File
import com.sorrowblue.comicviewer.data.database.entity.ReadLater
import com.sorrowblue.comicviewer.data.datasource.ReadLaterLocalDataSource
import com.sorrowblue.comicviewer.framework.Result
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal class ReadLaterLocalDataSourceImpl @Inject constructor(
    private val readLaterDao: ReadLaterDao
) : ReadLaterLocalDataSource {

    override suspend fun add(model: ReadLaterModel): Result<ReadLaterModel, Unit> {
        return kotlin.runCatching {
            readLaterDao.upsert(ReadLater(model.serverModelId.value, model.path))
        }.fold({
            Result.Success(model)
        }, {
            Result.Error(Unit)
        })
    }

    override fun pagingDataFlow(pagingConfig: PagingConfig): Flow<PagingData<FileModel>> {
        return Pager(pagingConfig) { readLaterDao.pagingSource() }.flow
            .map { it.map(File::toFileModel) }
    }
}
