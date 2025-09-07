package com.sorrowblue.comicviewer.data.database.impl

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.sorrowblue.comicviewer.data.database.dao.ReadLaterFileDao
import com.sorrowblue.comicviewer.data.database.entity.file.FileEntity
import com.sorrowblue.comicviewer.data.database.entity.readlater.ReadLaterFileEntity
import com.sorrowblue.comicviewer.domain.model.ReadLaterFile
import com.sorrowblue.comicviewer.domain.model.Resource
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.service.datasource.ReadLaterFileLocalDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.koin.core.annotation.Single

@Single
internal class ReadLaterFileLocalDataSourceImpl(
    private val readLaterFileDao: ReadLaterFileDao,
) : ReadLaterFileLocalDataSource {

    override suspend fun updateOrAdd(file: ReadLaterFile): Resource<ReadLaterFile, Resource.SystemError> {
        return kotlin.runCatching {
            readLaterFileDao.upsert(ReadLaterFileEntity.fromModel(file))
        }.fold(
            onSuccess = { Resource.Success(file) },
            onFailure = { Resource.Error(Resource.SystemError(it)) }
        )
    }

    override fun exists(file: ReadLaterFile): Resource<Flow<Boolean>, Resource.SystemError> {
        return kotlin.runCatching {
            readLaterFileDao.exists(file.bookshelfId.value, file.path)
        }.fold(
            onSuccess = { Resource.Success(it) },
            onFailure = { Resource.Error(Resource.SystemError(it)) }
        )
    }

    override suspend fun delete(file: ReadLaterFile): Resource<Unit, Resource.SystemError> {
        return kotlin.runCatching {
            readLaterFileDao.delete(ReadLaterFileEntity.fromModel(file))
        }.fold({
            Resource.Success(Unit)
        }, {
            Resource.Error(Resource.SystemError(it))
        })
    }

    override suspend fun deleteAll(): Resource<Unit, Resource.SystemError> {
        return kotlin.runCatching {
            readLaterFileDao.deleteAll()
        }.fold({
            Resource.Success(Unit)
        }, {
            Resource.Error(Resource.SystemError(it))
        })
    }

    override fun pagingDataFlow(pagingConfig: PagingConfig): Flow<PagingData<File>> {
        return Pager(pagingConfig) { readLaterFileDao.pagingSourceReadLaterFile() }.flow
            .map { it.map(FileEntity::toModel) }
    }
}
