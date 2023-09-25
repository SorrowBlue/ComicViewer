package com.sorrowblue.comicviewer.domain.interactor.paging

import androidx.paging.PagingData
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.repository.FileRepository
import com.sorrowblue.comicviewer.domain.usecase.paging.PagingHistoryBookUseCase
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

internal class PagingHistoryBookInteractor @Inject constructor(
    private val repository: FileRepository
) : PagingHistoryBookUseCase() {

    override fun run(request: Request): Flow<PagingData<File>> {
        return repository.pagingHistoryBookFlow(request.pagingConfig)
    }
}
