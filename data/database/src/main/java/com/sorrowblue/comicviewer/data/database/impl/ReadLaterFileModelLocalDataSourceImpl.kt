package com.sorrowblue.comicviewer.data.database.impl

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.sorrowblue.comicviewer.data.database.dao.ReadLaterFileDao
import com.sorrowblue.comicviewer.data.database.entity.FileEntity
import com.sorrowblue.comicviewer.data.database.entity.ReadLaterFileEntity
import com.sorrowblue.comicviewer.domain.model.ReadLaterFile
import com.sorrowblue.comicviewer.domain.model.Result
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.service.datasource.ReadLaterFileModelLocalDataSource
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal class ReadLaterFileModelLocalDataSourceImpl @Inject constructor(
    private val readLaterFileDao: ReadLaterFileDao,
) : ReadLaterFileModelLocalDataSource {

    override suspend fun add(model: ReadLaterFile): Result<ReadLaterFile, Unit> {
        return kotlin.runCatching {
            readLaterFileDao.upsert(ReadLaterFileEntity.fromModel(model))
        }.fold({
            Result.Success(model)
        }, {
            Result.Error(Unit)
        })
    }

    override fun exists(model: ReadLaterFile): Flow<Boolean> {
        return readLaterFileDao.exists(model.bookshelfId.value, model.path)
    }

    override suspend fun delete(model: ReadLaterFile): Result<ReadLaterFile, Unit> {
        return kotlin.runCatching {
            readLaterFileDao.delete(ReadLaterFileEntity.fromModel(model))
        }.fold({
            Result.Success(model)
        }, {
            Result.Error(Unit)
        })
    }

    override suspend fun deleteAll(): Result<Unit, Unit> {
        return kotlin.runCatching {
            readLaterFileDao.deleteAll()
        }.fold({
            Result.Success(Unit)
        }, {
            Result.Error(Unit)
        })
    }

    override fun pagingDataFlow(pagingConfig: PagingConfig): Flow<PagingData<File>> {
        return Pager(pagingConfig) { readLaterFileDao.pagingSource() }.flow
            .map { it.map(FileEntity::toModel) }
    }
}
