package com.sorrowblue.comicviewer.domain.service.datasource

import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.sorrowblue.comicviewer.domain.model.Resource
import com.sorrowblue.comicviewer.domain.model.SearchCondition
import com.sorrowblue.comicviewer.domain.model.bookshelf.Bookshelf
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.file.Book
import com.sorrowblue.comicviewer.domain.model.file.BookThumbnail
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.model.file.Folder
import com.sorrowblue.comicviewer.domain.model.settings.folder.FolderThumbnailOrder
import com.sorrowblue.comicviewer.domain.model.settings.folder.SortType
import kotlinx.coroutines.flow.Flow

sealed interface LocalDataSourceQueryError : Resource.IError {
    data object NotFound : LocalDataSourceQueryError
    data class SystemError(val throwable: Throwable) : LocalDataSourceQueryError
}

interface FileLocalDataSource {

    fun pagingSource(
        pagingConfig: PagingConfig,
        bookshelfId: BookshelfId,
        searchCondition: () -> SearchCondition,
    ): Flow<PagingData<File>>

    /**
     * Add files. If it already exists, update it.
     *
     * @param fileModel
     */
    suspend fun addUpdate(fileModel: File)

    /**
     * Update reading history.
     *
     * @param path
     * @param bookshelfId
     * @param lastReadPage Last page read
     * @param lastReading Last read time
     */
    suspend fun updateHistory(
        path: String,
        bookshelfId: BookshelfId,
        lastReadPage: Int,
        lastReading: Long,
    )

    /**
     * Update additional information in the file.
     *
     * @param path
     * @param bookshelfId
     * @param cacheKey
     * @param totalPage
     */
    suspend fun updateAdditionalInfo(
        path: String,
        bookshelfId: BookshelfId,
        cacheKey: String,
        totalPage: Int,
    )

    suspend fun updateSimpleAll(list: List<File>)
    suspend fun updateSimple(list: File): Resource<File, LocalDataSourceQueryError>

    suspend fun selectByNotPaths(
        bookshelfId: BookshelfId,
        path: String,
        list: List<String>,
    ): List<File>

    /**
     * Delete all files.
     *
     * @param list
     */
    suspend fun deleteAll(list: List<File>)

    /**
     * Returns true if the file exists.
     *
     * @param bookshelfId
     * @param path
     * @return true if the file exists
     */
    suspend fun exists(bookshelfId: BookshelfId, path: String): Boolean

    fun pagingSource(
        pagingConfig: PagingConfig,
        bookshelf: Bookshelf,
        file: File,
        searchCondition: () -> SearchCondition,
    ): Flow<PagingData<File>>

    fun pagingSourceBookThumbnail(
        pagingConfig: PagingConfig,
        bookshelf: Bookshelf,
        file: File,
        searchCondition: () -> SearchCondition,
    ): Flow<PagingData<BookThumbnail>>

    fun flow(bookshelfId: BookshelfId, path: String): Flow<File?>
    suspend fun findBy(bookshelfId: BookshelfId, path: String): File?
    fun nextFileModel(bookshelfId: BookshelfId, path: String, sortType: SortType): Flow<File?>

    fun prevFileModel(
        bookshelfId: BookshelfId,
        path: String,
        sortType: SortType,
    ): Flow<File?>

    suspend fun getCacheKeys(
        bookshelfId: BookshelfId,
        parent: String,
        limit: Int,
        folderThumbnailOrderModel: FolderThumbnailOrder,
    ): List<String>

    suspend fun removeCacheKey(diskCacheKey: String)

    suspend fun root(id: BookshelfId): Folder?

    fun pagingHistoryBookSource(pagingConfig: PagingConfig): Flow<PagingData<Book>>

    suspend fun deleteThumbnails()
    suspend fun deleteHistory(bookshelfId: BookshelfId, list: List<String>)
    suspend fun updateHistory(file: File, files: List<File>)
    suspend fun deleteAll2(bookshelfModelId: BookshelfId)
    suspend fun getCacheKeyList(bookshelfId: BookshelfId): List<String>
    fun lastHistory(): Flow<File>
}
