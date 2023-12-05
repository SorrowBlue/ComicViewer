package com.sorrowblue.comicviewer.domain.service.interactor.paging

import androidx.paging.PagingData
import com.sorrowblue.comicviewer.domain.model.file.Book
import com.sorrowblue.comicviewer.domain.service.repository.FileRepository
import com.sorrowblue.comicviewer.domain.usecase.paging.PagingHistoryBookUseCase
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

internal class PagingHistoryBookInteractor @Inject constructor(
    private val repository: FileRepository,
) : PagingHistoryBookUseCase() {

    override fun run(request: Request): Flow<PagingData<Book>> {
        return repository.pagingHistoryBookFlow(request.pagingConfig)
    }
}
