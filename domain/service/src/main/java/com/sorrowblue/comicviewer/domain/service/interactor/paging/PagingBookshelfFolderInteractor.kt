package com.sorrowblue.comicviewer.domain.service.interactor.paging

import androidx.paging.PagingData
import com.sorrowblue.comicviewer.domain.model.BookshelfFolder
import com.sorrowblue.comicviewer.domain.service.repository.BookshelfRepository
import com.sorrowblue.comicviewer.domain.usecase.paging.PagingBookshelfFolderUseCase
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

internal class PagingBookshelfFolderInteractor @Inject constructor(
    private val repository: BookshelfRepository,
) : PagingBookshelfFolderUseCase() {

    override fun run(request: Request): Flow<PagingData<BookshelfFolder>> {
        return repository.pagingDataFlow(request.pagingConfig)
    }
}
