/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.repository

import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.sorrowblue.comicviewer.domain.model.bookshelf.Bookshelf
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.common.Resource
import com.sorrowblue.comicviewer.domain.model.file.Book
import com.sorrowblue.comicviewer.domain.model.file.BookThumbnail
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.model.file.Folder
import com.sorrowblue.comicviewer.domain.model.search.SearchCondition
import com.sorrowblue.comicviewer.domain.model.settings.folder.FolderThumbnailOrder
import com.sorrowblue.comicviewer.domain.model.settings.folder.SortType
import kotlinx.coroutines.flow.Flow

sealed interface FileRepositoryQueryError : Resource.IError {
    data object NotFound : FileRepositoryQueryError

    data class SystemError(val throwable: Throwable) : FileRepositoryQueryError
}

interface FileRepository {
    fun pagingDataFlow(
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

    fun pagingDataFlow(
        pagingConfig: PagingConfig,
        bookshelfId: BookshelfId?,
        searchCondition: () -> SearchCondition,
    ): Flow<PagingData<File>>

    suspend fun addUpdate(fileModel: File)

    suspend fun updateHistory(
        path: String,
        bookshelfId: BookshelfId,
        lastReadPage: Int,
        lastReading: Long,
    )

    suspend fun updateAdditionalInfo(
        path: String,
        bookshelfId: BookshelfId,
        cacheKey: String,
        totalPage: Int,
    )

    suspend fun updateSimpleAll(list: List<File>)

    suspend fun updateSimple(list: File): Resource<File, FileRepositoryQueryError>

    suspend fun selectByNotPaths(
        bookshelfId: BookshelfId,
        path: String,
        list: List<String>,
    ): List<File>

    suspend fun deleteAll(list: List<File>)

    suspend fun exists(bookshelfId: BookshelfId, path: String): Boolean

    fun pagingSource(
        bookshelfId: BookshelfId,
        pagingConfig: PagingConfig,
    ): Flow<PagingData<BookThumbnail>>

    fun flow(bookshelfId: BookshelfId, path: String): Flow<File?>

    suspend fun findBy(bookshelfId: BookshelfId, path: String): File?

    fun nextFileModel(bookshelfId: BookshelfId, path: String, sortType: SortType): Flow<File?>

    fun nextFileModel(
        bookshelfId: BookshelfId,
        path: String,
        searchCondition: SearchCondition,
        sortType: SortType,
    ): Flow<File?>

    fun prevFileModel(bookshelfId: BookshelfId, path: String, sortType: SortType): Flow<File?>

    fun prevFileModel(
        bookshelfId: BookshelfId,
        path: String,
        searchCondition: SearchCondition,
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

    suspend fun clearCacheKey(bookshelfId: BookshelfId)

    suspend fun deleteHistory(bookshelfId: BookshelfId, list: List<String>)

    suspend fun deleteAllHistory()

    suspend fun updateHistory(file: File, files: List<File>)

    suspend fun deleteAll2(bookshelfModelId: BookshelfId)

    suspend fun getCacheKeyList(bookshelfId: BookshelfId): List<String>

    fun lastHistory(): Flow<File?>

    suspend fun fileList(bookshelfId: BookshelfId, limit: Int, offset: Long): List<File>

    suspend fun count(bookshelfId: BookshelfId): Long

    suspend fun updateFileType(file: File)
}
