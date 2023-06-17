package com.sorrowblue.comicviewer.data.datasource

import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.sorrowblue.comicviewer.data.common.FileModel
import com.sorrowblue.comicviewer.data.common.SimpleFileModel
import com.sorrowblue.comicviewer.data.common.bookshelf.BookshelfModel
import com.sorrowblue.comicviewer.data.common.bookshelf.BookshelfModelId
import com.sorrowblue.comicviewer.data.common.bookshelf.FolderThumbnailOrderModel
import com.sorrowblue.comicviewer.data.common.bookshelf.SearchConditionEntity
import com.sorrowblue.comicviewer.data.common.bookshelf.SortEntity
import kotlinx.coroutines.flow.Flow

interface FileModelLocalDataSource {

    /**
     * Add files. If it already exists, update it.
     *
     * @param fileModel
     */
    suspend fun addUpdate(fileModel: FileModel)

    /**
     * Update reading history.
     *
     * @param path
     * @param bookshelfModelId
     * @param lastReadPage Last page read
     * @param lastReading Last read time
     */
    suspend fun updateHistory(
        path: String,
        bookshelfModelId: BookshelfModelId,
        lastReadPage: Int,
        lastReading: Long
    )

    /**
     * Update additional information in the file.
     *
     * @param path
     * @param bookshelfModelId
     * @param cacheKey
     * @param totalPage
     */
    suspend fun updateAdditionalInfo(
        path: String,
        bookshelfModelId: BookshelfModelId,
        cacheKey: String,
        totalPage: Int
    )

    suspend fun updateSimpleAll(list: List<SimpleFileModel>)

    suspend fun selectByNotPaths(
        bookshelfModelId: BookshelfModelId,
        path: String,
        list: List<String>
    ): List<FileModel>

    /**
     * Delete all files.
     *
     * @param list
     */
    suspend fun deleteAll(list: List<FileModel>)

    /**
     * Returns true if the file exists.
     *
     * @param bookshelfModelId
     * @param path
     * @return true if the file exists
     */
    suspend fun exists(bookshelfModelId: BookshelfModelId, path: String): Boolean

    fun pagingSource(
        pagingConfig: PagingConfig,
        bookshelfModel: BookshelfModel,
        fileModel: FileModel,
        sortType: () -> SortEntity
    ): Flow<PagingData<FileModel>>

    fun flow(bookshelfModelId: BookshelfModelId, path: String): Flow<FileModel?>
    suspend fun findBy(bookshelfModelId: BookshelfModelId, path: String): FileModel?
    fun nextFileModel(
        bookshelfModelId: BookshelfModelId,
        path: String,
        sortEntity: SortEntity
    ): Flow<FileModel?>

    fun prevFileModel(
        bookshelfModelId: BookshelfModelId,
        path: String,
        sortEntity: SortEntity
    ): Flow<FileModel?>

    suspend fun getCacheKeys(
        bookshelfModelId: BookshelfModelId,
        parent: String,
        limit: Int
    ): List<String>

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
        searchConditionEntity: SearchConditionEntity,
        sortEntity: () -> SortEntity
    ): Flow<PagingData<FileModel>>

    suspend fun root(id: BookshelfModelId): FileModel.Folder?

    fun pagingHistoryBookSource(pagingConfig: PagingConfig): Flow<PagingData<FileModel>>

    suspend fun deleteThumbnails()
    suspend fun deleteHistory(bookshelfModelId: BookshelfModelId, list: List<String>)
    suspend fun updateHistory(fileModel: FileModel, files: List<FileModel>)
}
