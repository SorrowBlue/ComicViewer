package com.sorrowblue.comicviewer.domain.service.datasource

import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.sorrowblue.comicviewer.domain.model.BookshelfFolder
import com.sorrowblue.comicviewer.domain.model.Resource
import com.sorrowblue.comicviewer.domain.model.bookshelf.Bookshelf
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.file.BookThumbnail
import kotlinx.coroutines.flow.Flow

interface BookshelfLocalDataSource {

    suspend fun updateOrCreate(bookshelf: Bookshelf): Bookshelf?

    suspend fun delete(bookshelfId: BookshelfId): Resource<Unit, Resource.SystemError>

    fun flow(bookshelfId: BookshelfId): Flow<Bookshelf?>

    fun pagingSource(pagingConfig: PagingConfig): Flow<PagingData<BookshelfFolder>>

    fun pagingSource(
        bookshelfId: BookshelfId,
        pagingConfig: PagingConfig,
    ): Flow<PagingData<BookThumbnail>>

    fun allBookshelf(): Resource<Flow<List<Bookshelf>>, Resource.SystemError>
    suspend fun updateDeleted(bookshelfId: BookshelfId, isDeleted: Boolean)
}
