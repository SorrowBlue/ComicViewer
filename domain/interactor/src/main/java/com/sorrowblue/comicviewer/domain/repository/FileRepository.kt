package com.sorrowblue.comicviewer.domain.repository

import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.sorrowblue.comicviewer.domain.entity.file.Book
import com.sorrowblue.comicviewer.domain.entity.file.File
import com.sorrowblue.comicviewer.domain.entity.file.Folder
import com.sorrowblue.comicviewer.domain.entity.server.Server
import com.sorrowblue.comicviewer.domain.entity.server.ServerId
import com.sorrowblue.comicviewer.domain.model.Response
import com.sorrowblue.comicviewer.domain.model.ScanType
import com.sorrowblue.comicviewer.framework.Result
import kotlinx.coroutines.flow.Flow

interface FileRepository {

    suspend fun update(serverId: ServerId, path: String, lastReadPage: Int, lastReadTime: Long)

    fun pagingDataFlow(
        pagingConfig: PagingConfig,
        server: Server,
        folder: Folder,
    ): Flow<PagingData<File>>

    suspend fun get(serverId: ServerId, path: String): Response<File?>
    fun getFile(serverId: ServerId, path: String): Flow<Result<File, Unit>>
    suspend fun getBook(serverId: ServerId, path: String): Response<Book?>
    suspend fun scan(folder: Folder, scanType: ScanType): String
    fun pagingDataFlow(
        pagingConfig: PagingConfig,
        server: Server,
        query: () -> String
    ): Flow<PagingData<File>>

    suspend fun get2(serverId: ServerId, path: String): Result<File?, Unit>
    suspend fun getRoot(serverId: ServerId): Result<File?, Unit>
    fun getNextRelFile(
        serverId: ServerId,
        path: String,
        isNext: Boolean
    ): Flow<Result<File, Unit>>

    suspend fun list(serverId: ServerId): List<File>
    suspend fun getFolder(server: Server, path: String): Result<Folder, FileRepositoryError>
}
