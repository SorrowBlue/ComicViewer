package com.sorrowblue.comicviewer.data.datasource

import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.sorrowblue.comicviewer.data.common.FileModel
import com.sorrowblue.comicviewer.data.common.FolderThumbnailOrderModel
import com.sorrowblue.comicviewer.data.common.ServerModel
import com.sorrowblue.comicviewer.data.common.ServerModelId
import com.sorrowblue.comicviewer.data.common.SimpleFileModel
import com.sorrowblue.comicviewer.data.common.SortType
import kotlinx.coroutines.flow.Flow

interface FileModelLocalDataSource {
    suspend fun register(fileModel: FileModel)

    suspend fun update(
        path: String,
        serverModelId: ServerModelId,
        lastReadPage: Int,
        lastRead: Long
    )

    suspend fun update(path: String, serverModelId: ServerModelId, cacheKey: String, totalPage: Int)

    suspend fun updateAll(list: List<SimpleFileModel>)

    suspend fun <R> withTransaction(block: suspend () -> R): R

    suspend fun selectByNotPaths(
        serverModelId: ServerModelId,
        path: String,
        list: List<String>
    ): List<FileModel>

    suspend fun deleteAll(list: List<FileModel>)
    suspend fun exists(serverModelId: ServerModelId, path: String): Boolean
    suspend fun registerAll(list: List<FileModel>)
    fun pagingSource(
        pagingConfig: PagingConfig,
        serverModel: ServerModel,
        fileModel: FileModel,
        sortType: () -> SortType
    ): Flow<PagingData<FileModel>>

    suspend fun findBy(serverModelId: ServerModelId): List<FileModel>
    suspend fun findBy(serverModelId: ServerModelId, path: String): FileModel?
    suspend fun nextFileModel(serverModelId: ServerModelId, path: String): FileModel?
    suspend fun prevFileModel(serverModelId: ServerModelId, path: String): FileModel?
    suspend fun getCacheKeys(serverModelId: ServerModelId, parent: String, limit: Int): List<String>
    suspend fun getCacheKeys(
        serverModelId: ServerModelId,
        parent: String,
        limit: Int,
        folderThumbnailOrderModel: FolderThumbnailOrderModel
    ): List<String>

    suspend fun removeCacheKey(diskCacheKey: String)
    fun pagingSource(
        pagingConfig: PagingConfig,
        serverModelId: ServerModelId,
        query: () -> String,
        sortType: () -> SortType
    ): Flow<PagingData<FileModel>>

    suspend fun root(id: ServerModelId): FileModel.Folder?
}
