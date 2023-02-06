package com.sorrowblue.comicviewer.data.database.impl

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.sorrowblue.comicviewer.data.common.FileModel
import com.sorrowblue.comicviewer.data.common.ReadLaterFileModel
import com.sorrowblue.comicviewer.data.database.dao.ReadLaterFileDao
import com.sorrowblue.comicviewer.data.database.entity.File
import com.sorrowblue.comicviewer.data.database.entity.ReadLaterFile
import com.sorrowblue.comicviewer.data.datasource.ReadLaterFileModelLocalDataSource
import com.sorrowblue.comicviewer.framework.Result
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal class ReadLaterFileModelLocalDataSourceImpl @Inject constructor(
    private val readLaterFileDao: ReadLaterFileDao
) : ReadLaterFileModelLocalDataSource {

    override suspend fun add(model: ReadLaterFileModel): Result<ReadLaterFileModel, Unit> {
        return kotlin.runCatching {
            readLaterFileDao.upsert(ReadLaterFile.fromModel(model))
        }.fold({
            Result.Success(model)
        }, {
            Result.Error(Unit)
        })
    }

    override fun pagingDataFlow(pagingConfig: PagingConfig): Flow<PagingData<FileModel>> {
        return Pager(pagingConfig) { readLaterFileDao.pagingSource() }.flow
            .map { it.map(File::toModel) }
    }
}
