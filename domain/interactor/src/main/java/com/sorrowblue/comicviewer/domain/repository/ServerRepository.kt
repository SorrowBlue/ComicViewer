package com.sorrowblue.comicviewer.domain.repository

import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.sorrowblue.comicviewer.domain.entity.Server
import com.sorrowblue.comicviewer.domain.entity.ServerBookshelf
import com.sorrowblue.comicviewer.domain.entity.ServerId
import com.sorrowblue.comicviewer.domain.model.Response
import com.sorrowblue.comicviewer.domain.model.Result
import com.sorrowblue.comicviewer.domain.usecase.RegisterLibraryError
import kotlinx.coroutines.flow.Flow

interface ServerRepository {

    fun pagingDataFlow(pagingConfig: PagingConfig): Flow<PagingData<ServerBookshelf>>
    suspend fun exists(server: Server, path: String): Result<Boolean, LibraryStatus>
    suspend fun registerOrUpdate(
        server: Server,
        path: String
    ): Result<Server, RegisterLibraryError>

    suspend fun get(serverId: ServerId): Result<Server?, LibraryStatus>
    suspend fun delete(server: Server): Response<Boolean>
}
