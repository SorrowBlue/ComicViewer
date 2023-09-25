package com.sorrowblue.comicviewer.data.infrastructure.datasource

import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.sorrowblue.comicviewer.data.model.BookshelfFolderModel
import com.sorrowblue.comicviewer.data.model.bookshelf.BookshelfModel
import com.sorrowblue.comicviewer.data.model.bookshelf.BookshelfModelId
import kotlinx.coroutines.flow.Flow

interface BookshelfLocalDataSource {

    suspend fun create(bookshelfModel: BookshelfModel): BookshelfModel

    suspend fun delete(bookshelfModel: BookshelfModel): Int

    fun flow(bookshelfModelId: BookshelfModelId): Flow<BookshelfModel?>

    fun pagingSource(pagingConfig: PagingConfig): Flow<PagingData<BookshelfFolderModel>>
}
