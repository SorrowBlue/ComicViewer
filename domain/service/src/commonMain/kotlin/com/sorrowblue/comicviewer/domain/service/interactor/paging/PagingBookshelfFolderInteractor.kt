package com.sorrowblue.comicviewer.domain.service.interactor.paging

import androidx.paging.PagingData
import com.sorrowblue.comicviewer.domain.model.BookshelfFolder
import com.sorrowblue.comicviewer.domain.model.file.BookThumbnail
import com.sorrowblue.comicviewer.domain.service.datasource.BookshelfLocalDataSource
import com.sorrowblue.comicviewer.domain.usecase.paging.PagingBookshelfBookUseCase
import com.sorrowblue.comicviewer.domain.usecase.paging.PagingBookshelfFolderUseCase
import di.Inject
import kotlinx.coroutines.flow.Flow
import org.koin.core.annotation.Singleton

@Singleton
internal class PagingBookshelfFolderInteractor @Inject constructor(
    private val bookshelfLocalDataSource: BookshelfLocalDataSource,
) : PagingBookshelfFolderUseCase() {

    override fun run(request: Request): Flow<PagingData<BookshelfFolder>> {
        return bookshelfLocalDataSource.pagingSource(request.pagingConfig)
    }
}

@Singleton
internal class PagingBookshelfBookInteractor @Inject constructor(
    private val bookshelfLocalDataSource: BookshelfLocalDataSource,
) : PagingBookshelfBookUseCase() {
    override fun run(request: Request): Flow<PagingData<BookThumbnail>> {
        return bookshelfLocalDataSource.pagingSource(request.bookshelfId, request.pagingConfig)
    }
}
