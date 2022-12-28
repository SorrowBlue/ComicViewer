package com.sorrowblue.comicviewer.domain.usecase.interactor

import androidx.paging.PagingData
import com.sorrowblue.comicviewer.domain.entity.File
import com.sorrowblue.comicviewer.domain.repository.FileRepository
import com.sorrowblue.comicviewer.domain.usecase.PagingQueryFileRequest
import com.sorrowblue.comicviewer.domain.usecase.PagingQueryFileUseCase
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

internal class PagingQueryFileUseCaseInteractor @Inject constructor(
    private val repository: FileRepository,
) : PagingQueryFileUseCase() {

    override fun run(request: PagingQueryFileRequest): Flow<PagingData<File>> {
        return repository.pagingDataFlow(request.pagingConfig, request.server, request.query)
    }
}
