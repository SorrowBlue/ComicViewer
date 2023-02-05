package com.sorrowblue.comicviewer.data.reporitory

import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.sorrowblue.comicviewer.data.common.FileModel
import com.sorrowblue.comicviewer.data.common.ReadLaterModel
import com.sorrowblue.comicviewer.data.common.ServerModelId
import com.sorrowblue.comicviewer.data.datasource.ReadLaterLocalDataSource
import com.sorrowblue.comicviewer.data.toFile
import com.sorrowblue.comicviewer.data.toServerId
import com.sorrowblue.comicviewer.domain.entity.ReadLater
import com.sorrowblue.comicviewer.domain.entity.file.File
import com.sorrowblue.comicviewer.domain.repository.ReadLaterRepository
import com.sorrowblue.comicviewer.framework.Result
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal class ReadLaterRepositoryImpl @Inject constructor(
    private val readLaterLocalDataSource: ReadLaterLocalDataSource
) : ReadLaterRepository {
    override suspend fun add(readLater: ReadLater): Result<ReadLater, Unit> {
        return readLaterLocalDataSource.add(readLater.toModel()).fold({
            Result.Success(it.toReadLater())
        }, {
            Result.Error(it)
        }, {
            Result.Exception(it)
        })
    }

    override fun pagingDataFlow(pagingConfig: PagingConfig): Flow<PagingData<File>> {
        return readLaterLocalDataSource.pagingDataFlow(pagingConfig)
            .map { it.map(FileModel::toFile) }
    }
}

fun ReadLater.toModel() = ReadLaterModel(ServerModelId(serverId.value), path)

fun ReadLaterModel.toReadLater() = ReadLater(serverModelId.toServerId(), path)
