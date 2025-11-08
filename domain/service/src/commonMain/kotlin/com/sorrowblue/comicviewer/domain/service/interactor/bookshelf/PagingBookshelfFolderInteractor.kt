package com.sorrowblue.comicviewer.domain.service.interactor.bookshelf

import androidx.paging.PagingData
import com.sorrowblue.comicviewer.domain.model.BookshelfFolder
import com.sorrowblue.comicviewer.domain.service.datasource.BookshelfLocalDataSource
import com.sorrowblue.comicviewer.domain.usecase.bookshelf.PagingBookshelfFolderUseCase
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.Flow

@Inject
internal class PagingBookshelfFolderInteractor(
    private val bookshelfLocalDataSource: BookshelfLocalDataSource,
) : PagingBookshelfFolderUseCase() {
    override fun run(request: Request): Flow<PagingData<BookshelfFolder>> =
        bookshelfLocalDataSource.pagingSource(request.pagingConfig)
}
