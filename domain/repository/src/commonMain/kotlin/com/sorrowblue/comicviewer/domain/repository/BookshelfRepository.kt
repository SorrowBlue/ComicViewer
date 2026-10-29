/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.repository

import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.sorrowblue.comicviewer.domain.model.bookshelf.Bookshelf
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfFolder
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.common.Resource
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for managing [Bookshelf] entities.
 */
interface BookshelfRepository {
    /**
     * Inserts or updates a [bookshelf] and executes [transaction] with the persisted entity.
     */
    suspend fun updateOrCreate(
        bookshelf: Bookshelf,
        transaction: suspend (Bookshelf) -> Unit,
    ): Bookshelf?

    /**
     * Observes a single [Bookshelf] by its [bookshelfId].
     */
    fun flow(bookshelfId: BookshelfId): Flow<Bookshelf?>

    /**
     * Observes paginated [BookshelfFolder] items.
     */
    fun pagingSource(pagingConfig: PagingConfig): Flow<PagingData<BookshelfFolder>>

    /**
     * Observes the list of all registered [Bookshelf] items.
     */
    fun allBookshelf(): Resource<Flow<List<Bookshelf>>, Resource.SystemError>

    /**
     * Updates the soft-deletion state [isDeleted] of the bookshelf identified by [bookshelfId].
     */
    suspend fun updateDeleted(bookshelfId: BookshelfId, isDeleted: Boolean)
}
