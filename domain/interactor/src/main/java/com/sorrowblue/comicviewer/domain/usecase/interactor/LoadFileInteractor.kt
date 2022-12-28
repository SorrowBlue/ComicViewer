package com.sorrowblue.comicviewer.domain.usecase.interactor

import androidx.paging.PagingData
import com.sorrowblue.comicviewer.domain.usecase.LoadFileRequest
import com.sorrowblue.comicviewer.domain.entity.File
import com.sorrowblue.comicviewer.domain.repository.FileRepository
import com.sorrowblue.comicviewer.domain.usecase.LoadFileUseCase
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

internal class LoadFileInteractor @Inject constructor(
    private val repository: FileRepository,
) : LoadFileUseCase() {

    override fun run(request: LoadFileRequest): Flow<PagingData<File>> {
        return repository.pagingDataFlow(request.pagingConfig, request.server, request.bookshelf)
    }
}
