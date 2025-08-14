package com.sorrowblue.comicviewer.domain.service.interactor.file

import androidx.paging.PagingData
import com.sorrowblue.comicviewer.domain.model.file.BookThumbnail
import com.sorrowblue.comicviewer.domain.service.datasource.FileLocalDataSource
import com.sorrowblue.comicviewer.domain.usecase.file.PagingBookshelfBookUseCase
import kotlinx.coroutines.flow.Flow
import org.koin.core.annotation.Factory

@Factory
internal class PagingBookshelfBookInteractor(
    private val fileLocalDataSource: FileLocalDataSource,
) : PagingBookshelfBookUseCase() {
    override fun run(request: Request): Flow<PagingData<BookThumbnail>> {
        return fileLocalDataSource.pagingSource(request.bookshelfId, request.pagingConfig)
    }
}
