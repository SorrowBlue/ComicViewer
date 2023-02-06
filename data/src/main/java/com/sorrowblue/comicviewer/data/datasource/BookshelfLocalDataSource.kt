package com.sorrowblue.comicviewer.data.datasource

import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.sorrowblue.comicviewer.data.common.BookshelfFolderModel
import com.sorrowblue.comicviewer.data.common.bookshelf.BookshelfModel
import com.sorrowblue.comicviewer.data.common.bookshelf.BookshelfModelId
import kotlinx.coroutines.flow.Flow

interface BookshelfLocalDataSource {

    suspend fun create(bookshelfModel: BookshelfModel): BookshelfModel

    suspend fun delete(bookshelfModel: BookshelfModel): Int

    fun get(bookshelfModelId: BookshelfModelId): Flow<BookshelfModel?>

    fun pagingSource(pagingConfig: PagingConfig): Flow<PagingData<BookshelfFolderModel>>
}
