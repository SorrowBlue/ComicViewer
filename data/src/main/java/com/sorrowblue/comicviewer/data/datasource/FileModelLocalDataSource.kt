package com.sorrowblue.comicviewer.data.datasource

import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.sorrowblue.comicviewer.data.common.FileModel
import com.sorrowblue.comicviewer.data.common.bookshelf.FolderThumbnailOrderModel
import com.sorrowblue.comicviewer.data.common.bookshelf.BookshelfModel
import com.sorrowblue.comicviewer.data.common.bookshelf.BookshelfModelId
import com.sorrowblue.comicviewer.data.common.SimpleFileModel
import com.sorrowblue.comicviewer.data.common.bookshelf.SortType
import kotlinx.coroutines.flow.Flow

interface FileModelLocalDataSource {
    suspend fun register(fileModel: FileModel)

    suspend fun update(
        path: String,
        bookshelfModelId: BookshelfModelId,
        lastReadPage: Int,
        lastRead: Long
    )

    suspend fun update(path: String, bookshelfModelId: BookshelfModelId, cacheKey: String, totalPage: Int)

    suspend fun updateAll(list: List<SimpleFileModel>)

    suspend fun <R> withTransaction(block: suspend () -> R): R

    suspend fun selectByNotPaths(
        bookshelfModelId: BookshelfModelId,
        path: String,
        list: List<String>
    ): List<FileModel>

    suspend fun deleteAll(list: List<FileModel>)
    suspend fun exists(bookshelfModelId: BookshelfModelId, path: String): Boolean
    suspend fun registerAll(list: List<FileModel>)
    fun pagingSource(
        pagingConfig: PagingConfig,
        bookshelfModel: BookshelfModel,
        fileModel: FileModel,
        sortType: () -> SortType
    ): Flow<PagingData<FileModel>>

    suspend fun findBy(bookshelfModelId: BookshelfModelId): List<FileModel>
    fun selectBy(bookshelfModelId: BookshelfModelId, path: String): Flow<FileModel?>
    suspend fun findBy(bookshelfModelId: BookshelfModelId, path: String): FileModel?
    fun nextFileModel(bookshelfModelId: BookshelfModelId, path: String): Flow<FileModel?>
    fun prevFileModel(bookshelfModelId: BookshelfModelId, path: String): Flow<FileModel?>
    suspend fun getCacheKeys(bookshelfModelId: BookshelfModelId, parent: String, limit: Int): List<String>
    suspend fun getCacheKeys(
        bookshelfModelId: BookshelfModelId,
        parent: String,
        limit: Int,
        folderThumbnailOrderModel: FolderThumbnailOrderModel
    ): List<String>

    suspend fun removeCacheKey(diskCacheKey: String)
    fun pagingSource(
        pagingConfig: PagingConfig,
        bookshelfModelId: BookshelfModelId,
        query: () -> String,
        sortType: () -> SortType
    ): Flow<PagingData<FileModel>>

    suspend fun root(id: BookshelfModelId): FileModel.Folder?
}
