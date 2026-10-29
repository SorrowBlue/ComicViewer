/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.repository

import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.collection.CollectionFile
import com.sorrowblue.comicviewer.domain.model.collection.CollectionId
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.model.settings.folder.SortType
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for managing [CollectionFile] associations between collections and files.
 */
interface CollectionFileRepository {
    /**
     * Observes paginated [File] items belonging to the collection with [id], ordered by [sortType].
     */
    fun pagingDataFlow(
        id: CollectionId,
        pagingConfig: PagingConfig,
        sortType: () -> SortType,
    ): Flow<PagingData<File>>

    /**
     * Adds a [file] to a basic collection.
     */
    suspend fun add(file: CollectionFile)

    /**
     * Removes a [file] from a basic collection.
     */
    suspend fun remove(file: CollectionFile)

    /**
     * Observes the next [File] after [file] in the collection ordered by [sortType].
     */
    fun flowNextCollectionFile(file: CollectionFile, sortType: SortType): Flow<File?>

    /**
     * Observes the previous [File] before [file] in the collection ordered by [sortType].
     */
    fun flowPrevCollectionFile(file: CollectionFile, sortType: SortType): Flow<File?>

    /**
     * Returns up to [limit] pairs of bookshelf ID and thumbnail cache key for the collection with [id].
     */
    suspend fun getCacheKeyList(id: CollectionId, limit: Int): List<Pair<BookshelfId, String>>
}
