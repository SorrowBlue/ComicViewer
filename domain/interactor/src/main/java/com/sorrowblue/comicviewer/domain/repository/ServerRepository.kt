package com.sorrowblue.comicviewer.domain.repository

import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.sorrowblue.comicviewer.domain.entity.ServerFolder
import com.sorrowblue.comicviewer.domain.entity.file.Folder
import com.sorrowblue.comicviewer.domain.entity.server.Server
import com.sorrowblue.comicviewer.domain.entity.server.ServerId
import com.sorrowblue.comicviewer.domain.model.Response
import com.sorrowblue.comicviewer.domain.usecase.RegisterLibraryError
import com.sorrowblue.comicviewer.framework.Result
import kotlinx.coroutines.flow.Flow

interface ServerRepository {

    fun pagingDataFlow(pagingConfig: PagingConfig): Flow<PagingData<ServerFolder>>
    suspend fun exists(server: Server, path: String): Result<Boolean, ServerRepositoryStatus>
    suspend fun registerOrUpdate(server: Server, path: String): Result<Server, RegisterLibraryError>

    fun get(serverId: ServerId): Flow<Result<Server, LibraryStatus>>
    suspend fun delete(server: Server): Response<Boolean>
    suspend fun connect(server: Server, path: String): Result<Unit, ServerRepositoryError>
    suspend fun register(server: Server, folder: Folder): Result<Server, ServerRepositoryError>
}
