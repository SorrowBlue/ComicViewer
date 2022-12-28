package com.sorrowblue.comicviewer.domain.repository

import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.sorrowblue.comicviewer.domain.entity.Book
import com.sorrowblue.comicviewer.domain.model.Response
import com.sorrowblue.comicviewer.domain.model.Result
import com.sorrowblue.comicviewer.domain.model.ScanType
import com.sorrowblue.comicviewer.domain.entity.Bookshelf
import com.sorrowblue.comicviewer.domain.entity.File
import com.sorrowblue.comicviewer.domain.entity.Server
import com.sorrowblue.comicviewer.domain.entity.ServerId
import kotlinx.coroutines.flow.Flow

interface FileRepository {

    suspend fun update(serverId: ServerId, path: String, lastReadPage: Int, lastReadTime: Long)


    fun pagingDataFlow(
        pagingConfig: PagingConfig,
        server: Server,
        bookshelf: Bookshelf,
    ): Flow<PagingData<File>>

    suspend fun get(serverId: ServerId, path: String): Response<File?>
    suspend fun getBook(serverId: ServerId, path: String): Response<Book?>
    suspend fun scan(bookshelf: Bookshelf, scanType: ScanType): String
    fun pagingDataFlow(
        pagingConfig: PagingConfig,
        server: Server,
        query: () -> String
    ): Flow<PagingData<File>>

    suspend fun get2(serverId: ServerId, path: String): Result<File?, Unit>
    suspend fun getRoot(serverId: ServerId): Result<File?, Unit>
    suspend fun getNextRelFile(
        serverId: ServerId,
        path: String,
        isNext: Boolean
    ): Result<File?, Unit>

    suspend fun list(serverId: ServerId): List<File>
}

