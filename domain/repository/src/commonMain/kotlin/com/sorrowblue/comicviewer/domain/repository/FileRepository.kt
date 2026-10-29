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

/**
 * Query errors returned by [FileRepository] operations.
 */
sealed interface FileRepositoryQueryError : Resource.IError {
    /**
     * Indicates that the target file was not found.
     */
    data object NotFound : FileRepositoryQueryError

    /**
     * Indicates a system or database error occurred during the query.
     *
     * @property throwable The underlying cause of the error.
     */
    data class SystemError(val throwable: Throwable) : FileRepositoryQueryError
}

/**
 * Repository interface for managing [File], [Book], and [Folder] entities.
 */
interface FileRepository {
    /**
     * Observes paginated [File] items within a [file] folder on the given [bookshelf].
     */
    fun pagingDataFlow(
        pagingConfig: PagingConfig,
        bookshelf: Bookshelf,
        file: File,
        searchCondition: () -> SearchCondition,
    ): Flow<PagingData<File>>

    /**
     * Observes paginated [BookThumbnail] items within a [file] folder on the given [bookshelf].
     */
    fun pagingSourceBookThumbnail(
        pagingConfig: PagingConfig,
        bookshelf: Bookshelf,
        file: File,
        searchCondition: () -> SearchCondition,
    ): Flow<PagingData<BookThumbnail>>

    /**
     * Observes paginated [File] items matching [searchCondition], optionally scoped to [bookshelfId].
     */
    fun pagingDataFlow(
        pagingConfig: PagingConfig,
        bookshelfId: BookshelfId?,
        searchCondition: () -> SearchCondition,
    ): Flow<PagingData<File>>

    /**
     * Inserts or updates the given [file].
     */
    suspend fun addUpdate(file: File)

    /**
     * Updates the reading history for the file at [path] in [bookshelfId].
     */
    suspend fun updateHistory(
        path: String,
        bookshelfId: BookshelfId,
        lastReadPage: Int,
        lastReading: Long,
    )

    /**
     * Updates additional metadata (thumbnail [cacheKey] and [totalPage]) for the file at [path].
     */
    suspend fun updateAdditionalInfo(
        path: String,
        bookshelfId: BookshelfId,
        cacheKey: String,
        totalPage: Int,
    )

    /**
     * Updates basic file attributes for [file] and returns the updated [File].
     */
    suspend fun updateSimple(file: File): Resource<File, FileRepositoryQueryError>

    /**
     * Observes paginated [BookThumbnail] items belonging to [bookshelfId].
     */
    fun pagingSource(
        bookshelfId: BookshelfId,
        pagingConfig: PagingConfig,
    ): Flow<PagingData<BookThumbnail>>

    /**
     * Observes a single [File] by [bookshelfId] and [path].
     */
    fun flow(bookshelfId: BookshelfId, path: String): Flow<File?>

    /**
     * Finds a single [File] by [bookshelfId] and [path].
     */
    suspend fun findBy(bookshelfId: BookshelfId, path: String): File?

    /**
     * Observes the next sibling [File] after [path] ordered by [sortType].
     */
    fun nextFile(bookshelfId: BookshelfId, path: String, sortType: SortType): Flow<File?>

    /**
     * Observes the next [File] after [path] matching [searchCondition] ordered by [sortType].
     */
    fun nextFile(
        bookshelfId: BookshelfId,
        path: String,
        searchCondition: SearchCondition,
        sortType: SortType,
    ): Flow<File?>

    /**
     * Observes the previous sibling [File] before [path] ordered by [sortType].
     */
    fun prevFile(bookshelfId: BookshelfId, path: String, sortType: SortType): Flow<File?>

    /**
     * Observes the previous [File] before [path] matching [searchCondition] ordered by [sortType].
     */
    fun prevFile(
        bookshelfId: BookshelfId,
        path: String,
        searchCondition: SearchCondition,
        sortType: SortType,
    ): Flow<File?>

    /**
     * Retrieves thumbnail cache keys under [parent] in [bookshelfId] ordered by [folderThumbnailOrder].
     */
    suspend fun getCacheKeys(
        bookshelfId: BookshelfId,
        parent: String,
        limit: Int,
        folderThumbnailOrder: FolderThumbnailOrder,
    ): List<String>

    /**
     * Clears a specific [diskCacheKey] from stored files.
     */
    suspend fun removeCacheKey(diskCacheKey: String)

    /**
     * Finds the root [Folder] for the given bookshelf [id].
     */
    suspend fun root(id: BookshelfId): Folder?

    /**
     * Observes paginated [Book] items that have reading history.
     */
    fun pagingHistoryBookSource(pagingConfig: PagingConfig): Flow<PagingData<Book>>

    /**
     * Clears all thumbnail cache keys for [bookshelfId].
     */
    suspend fun clearCacheKey(bookshelfId: BookshelfId)

    /**
     * Deletes all reading history records.
     */
    suspend fun deleteAllHistory()

    /**
     * Synchronizes the child [files] under the parent [file] folder during a scan.
     */
    suspend fun syncFolderFiles(file: File, files: List<File>)

    /**
     * Deletes all files belonging to [bookshelfId].
     */
    suspend fun deleteAll(bookshelfId: BookshelfId)

    /**
     * Returns all non-empty thumbnail cache keys for [bookshelfId].
     */
    suspend fun getCacheKeyList(bookshelfId: BookshelfId): List<String>

    /**
     * Observes the most recently read [File].
     */
    fun lastHistory(): Flow<File?>

    /**
     * Returns a list of [File] items for [bookshelfId] with pagination [limit] and [offset].
     */
    suspend fun fileList(bookshelfId: BookshelfId, limit: Int, offset: Long): List<File>

    /**
     * Returns the total number of files for [bookshelfId].
     */
    suspend fun count(bookshelfId: BookshelfId): Long

    /**
     * Updates the persisted file type of [file].
     */
    suspend fun updateFileType(file: File)
}
