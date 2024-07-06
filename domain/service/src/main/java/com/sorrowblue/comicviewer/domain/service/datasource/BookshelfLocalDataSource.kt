package com.sorrowblue.comicviewer.domain.service.datasource

import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.sorrowblue.comicviewer.domain.model.BookshelfFolder
import com.sorrowblue.comicviewer.domain.model.bookshelf.Bookshelf
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import kotlinx.coroutines.flow.Flow

interface BookshelfLocalDataSource {

    suspend fun create(bookshelf: Bookshelf): Bookshelf

    suspend fun delete(bookshelf: Bookshelf): Int

    fun flow(bookshelfId: BookshelfId): Flow<Bookshelf?>

    fun pagingSource(pagingConfig: PagingConfig): Flow<PagingData<BookshelfFolder>>

    suspend fun allBookshelf(): List<Bookshelf>
}
