package com.sorrowblue.comicviewer.data.reporitory.impl

import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.sorrowblue.comicviewer.data.common.FileModel
import com.sorrowblue.comicviewer.data.common.ServerFileModelFolder
import com.sorrowblue.comicviewer.data.common.ServerModelId
import com.sorrowblue.comicviewer.data.datasource.FileModelLocalDataSource
import com.sorrowblue.comicviewer.data.datasource.FileModelRemoteDataSource
import com.sorrowblue.comicviewer.data.datasource.ServerLocalDataSource
import com.sorrowblue.comicviewer.data.toServer
import com.sorrowblue.comicviewer.data.toServerBookshelf
import com.sorrowblue.comicviewer.data.toServerModel
import com.sorrowblue.comicviewer.domain.model.Result
import com.sorrowblue.comicviewer.domain.entity.Server
import com.sorrowblue.comicviewer.domain.entity.ServerBookshelf
import com.sorrowblue.comicviewer.domain.entity.ServerId
import com.sorrowblue.comicviewer.domain.model.Response
import com.sorrowblue.comicviewer.domain.model.Unknown
import com.sorrowblue.comicviewer.domain.repository.LibraryStatus
import com.sorrowblue.comicviewer.domain.repository.ServerRepository
import com.sorrowblue.comicviewer.domain.usecase.RegisterLibraryError
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal class ServerRepositoryImpl @Inject constructor(
    private val serverLocalDataSource: ServerLocalDataSource,
    private val fileModelRemoteDataSource: FileModelRemoteDataSource.Factory,
    private val fileModelLocalDataSource: FileModelLocalDataSource,
    private val fileModelRemoteDataSourceFactory: FileModelRemoteDataSource.Factory
) : ServerRepository {

    override fun pagingDataFlow(pagingConfig: PagingConfig): Flow<PagingData<ServerBookshelf>> {
        return serverLocalDataSource.pagingSource(pagingConfig).map {
            it.map(ServerFileModelFolder::toServerBookshelf)
        }
    }

    override suspend fun exists(server: Server, path: String): Result<Boolean, LibraryStatus> {
        return Result.Success(
            fileModelRemoteDataSourceFactory.create(server.toServerModel()).exists(path)
        )
    }

    override suspend fun registerOrUpdate(
        server: Server,
        path: String
    ): Result<Server, RegisterLibraryError> {
        val serverModel = serverLocalDataSource.create(server.toServerModel())
        val fileModel = fileModelRemoteDataSource.create(serverModel).fileModel(path)
        when (fileModel) {
            is FileModel.File -> fileModel.copy(parent = "")
            is FileModel.Folder -> fileModel.copy(parent = "")
            is FileModel.ImageFolder -> fileModel.copy(parent = "")
        }.let {
            fileModelLocalDataSource.register(it)
        }
        return Result.Success(serverModel.toServer())
    }

    override suspend fun delete(server: Server): Response<Boolean> {
        serverLocalDataSource.delete(server.toServerModel())
        return Response.Success(true)
    }

    override suspend fun get(serverId: ServerId): Result<Server?, LibraryStatus> {
        return kotlin.runCatching {
            serverLocalDataSource.get(ServerModelId(serverId.value))
        }.fold({
            Result.Success(it?.toServer())
        }, {
            Result.Exception(Unknown)
        })
    }
}
