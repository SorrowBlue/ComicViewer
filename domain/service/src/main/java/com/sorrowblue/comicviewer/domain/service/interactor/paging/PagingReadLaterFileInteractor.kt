package com.sorrowblue.comicviewer.domain.service.interactor.paging

import androidx.paging.PagingData
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.service.repository.ReadLaterRepository
import com.sorrowblue.comicviewer.domain.usecase.paging.PagingReadLaterFileUseCase
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

internal class PagingReadLaterFileInteractor @Inject constructor(
    private val readLaterRepository: ReadLaterRepository
) : PagingReadLaterFileUseCase() {

    override fun run(request: Request): Flow<PagingData<File>> {
        return readLaterRepository.pagingDataFlow(request.pagingConfig)
    }
}
