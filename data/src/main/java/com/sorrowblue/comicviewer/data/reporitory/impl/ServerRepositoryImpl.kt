package com.sorrowblue.comicviewer.data.reporitory.impl

import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.sorrowblue.comicviewer.data.common.FileModel
import com.sorrowblue.comicviewer.data.common.ServerFileModelFolder
import com.sorrowblue.comicviewer.data.common.ServerModelId
import com.sorrowblue.comicviewer.data.datasource.FileModelLocalDataSource
import com.sorrowblue.comicviewer.data.datasource.RemoteDataSource
import com.sorrowblue.comicviewer.data.exception.RemoteException
import com.sorrowblue.comicviewer.data.datasource.ServerLocalDataSource
import com.sorrowblue.comicviewer.data.toFileModel
import com.sorrowblue.comicviewer.data.toServer
import com.sorrowblue.comicviewer.data.toServerBookshelf
import com.sorrowblue.comicviewer.data.toServerId
import com.sorrowblue.comicviewer.data.toServerModel
import com.sorrowblue.comicviewer.domain.entity.ServerBookshelf
import com.sorrowblue.comicviewer.domain.entity.file.Bookshelf
import com.sorrowblue.comicviewer.domain.entity.server.Server
import com.sorrowblue.comicviewer.domain.entity.server.ServerId
import com.sorrowblue.comicviewer.domain.model.Response
import com.sorrowblue.comicviewer.domain.repository.LibraryStatus
import com.sorrowblue.comicviewer.domain.repository.ServerRepository
import com.sorrowblue.comicviewer.domain.repository.ServerRepositoryError
import com.sorrowblue.comicviewer.domain.repository.ServerRepositoryStatus
import com.sorrowblue.comicviewer.domain.usecase.RegisterLibraryError
import com.sorrowblue.comicviewer.framework.NoNetwork
import com.sorrowblue.comicviewer.framework.Result
import com.sorrowblue.comicviewer.framework.Unknown
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import logcat.logcat

internal class ServerRepositoryImpl @Inject constructor(
    private val serverLocalDataSource: ServerLocalDataSource,
    private val fileModelLocalDataSource: FileModelLocalDataSource,
    private val remoteDataSourceFactory: RemoteDataSource.Factory
) : ServerRepository {

    override fun pagingDataFlow(pagingConfig: PagingConfig): Flow<PagingData<ServerBookshelf>> {
        return serverLocalDataSource.pagingSource(pagingConfig).map {
            it.map(ServerFileModelFolder::toServerBookshelf)
        }
    }

    override suspend fun exists(
        server: Server,
        path: String
    ): Result<Boolean, ServerRepositoryStatus> {
        return Result.Success(
            remoteDataSourceFactory.create(server.toServerModel()).exists(path)
        )
    }

    override suspend fun registerOrUpdate(
        server: Server,
        path: String
    ): Result<Server, RegisterLibraryError> {
        val serverModel = serverLocalDataSource.create(server.toServerModel())
        val fileModel = remoteDataSourceFactory.create(serverModel).fileModel(path)
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
            Result.Exception(Unknown(it))
        })
    }

    override suspend fun connect(server: Server, path: String): Result<Unit, ServerRepositoryError> {
        return withContext(Dispatchers.IO) {
            val remoteDataSource = remoteDataSourceFactory.create(server.toServerModel())
            kotlin.runCatching {
                remoteDataSource.connect(path)
                Result.Success(Unit)
            }.getOrElse {
                when (it as? RemoteException) {
                    RemoteException.InvalidAuth -> Result.Error(ServerRepositoryError.AuthenticationFailure)
                    RemoteException.InvalidServer -> Result.Error(ServerRepositoryError.IncorrectServerInfo)
                    RemoteException.NoNetwork -> Result.Exception(NoNetwork)
                    RemoteException.NotFound -> Result.Error(ServerRepositoryError.PathDoesNotExist)
                    null -> Result.Exception(Unknown(it))
                }
            }
        }
    }

    override suspend fun register(
        server: Server,
        bookshelf: Bookshelf
    ): Result<Server, ServerRepositoryError> {
        return withContext(Dispatchers.IO) {
            val r = serverLocalDataSource.create(server.toServerModel())
            val model = bookshelf.copy(serverId = r.id.toServerId(), parent = "").toFileModel()
            logcat { "model=${model}" }
            fileModelLocalDataSource.register(model)
            Result.Success(r.toServer())
        }
    }
}
