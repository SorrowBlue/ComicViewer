package com.sorrowblue.comicviewer.domain.service.datasource

import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.sorrowblue.comicviewer.domain.model.SearchCondition
import com.sorrowblue.comicviewer.domain.model.bookshelf.Bookshelf
import com.sorrowblue.comicviewer.domain.model.file.BookThumbnail
import com.sorrowblue.comicviewer.domain.model.file.File
import kotlinx.coroutines.flow.Flow

interface FileRemoteDataSource {
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
}
