package com.sorrowblue.comicviewer.domain.service.interactor.file

import androidx.paging.PagingData
import com.sorrowblue.comicviewer.domain.model.file.BookThumbnail
import com.sorrowblue.comicviewer.domain.service.datasource.BookshelfLocalDataSource
import com.sorrowblue.comicviewer.domain.usecase.file.PagingBookshelfBookUseCase
import kotlinx.coroutines.flow.Flow
import org.koin.core.annotation.Factory

@Factory
internal class PagingBookshelfBookInteractor(
    private val bookshelfLocalDataSource: BookshelfLocalDataSource,
) : PagingBookshelfBookUseCase() {
    override fun run(request: Request): Flow<PagingData<BookThumbnail>> {
        return bookshelfLocalDataSource.pagingSource(request.bookshelfId, request.pagingConfig)
    }
}
