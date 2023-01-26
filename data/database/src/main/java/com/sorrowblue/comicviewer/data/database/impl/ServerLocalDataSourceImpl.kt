package com.sorrowblue.comicviewer.data.database.impl

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.sorrowblue.comicviewer.data.common.FileModel
import com.sorrowblue.comicviewer.data.common.ServerFileModelFolder
import com.sorrowblue.comicviewer.data.common.ServerModel
import com.sorrowblue.comicviewer.data.common.ServerModelId
import com.sorrowblue.comicviewer.data.database.dao.ServerDao
import com.sorrowblue.comicviewer.data.database.entity.toServer
import com.sorrowblue.comicviewer.data.datasource.ServerLocalDataSource
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import logcat.logcat

internal class ServerLocalDataSourceImpl @Inject constructor(
    private val dao: ServerDao
) : ServerLocalDataSource {

    override suspend fun create(serverModel: ServerModel): ServerModel {
        val entity = serverModel.toServer()
        return dao.upsert(serverModel.toServer()).let {
            logcat { "dao.upsert(): before=${entity.id}, after=$it" }
            if (it == -1L) {
                entity
            } else {
                entity.copy(id = it.toInt())
            }
        }.toServerModel()
    }

    override suspend fun delete(serverModel: ServerModel): Int {
        return dao.delete(serverModel.toServer())
    }

    override suspend fun get(serverModelId: ServerModelId): ServerModel? {
        return dao.selectById(serverModelId.value)?.toServerModel()
    }

    override fun pagingSource(pagingConfig: PagingConfig): Flow<PagingData<ServerFileModelFolder>> {
        return Pager(pagingConfig) { dao.pagingSource() }.flow.map { pagingData ->
            pagingData.map {
                ServerFileModelFolder(it.server.toServerModel() to it.file.toFileModel() as FileModel.Folder)
            }
        }

    }
}

