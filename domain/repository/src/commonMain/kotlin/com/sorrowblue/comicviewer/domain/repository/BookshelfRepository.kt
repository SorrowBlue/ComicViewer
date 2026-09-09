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

interface BookshelfRepository {
    suspend fun updateOrCreate(
        bookshelf: Bookshelf,
        transaction: suspend (Bookshelf) -> Unit,
    ): Bookshelf?

    suspend fun delete(bookshelfId: BookshelfId): Resource<Unit, Resource.SystemError>

    fun flow(bookshelfId: BookshelfId): Flow<Bookshelf?>

    fun pagingSource(pagingConfig: PagingConfig): Flow<PagingData<BookshelfFolder>>

    fun allBookshelf(): Resource<Flow<List<Bookshelf>>, Resource.SystemError>

    suspend fun updateDeleted(bookshelfId: BookshelfId, isDeleted: Boolean)
}
