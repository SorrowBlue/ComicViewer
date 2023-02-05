package com.sorrowblue.comicviewer.domain.usecase.interactor

import androidx.paging.PagingData
import com.sorrowblue.comicviewer.domain.entity.file.File
import com.sorrowblue.comicviewer.domain.repository.ReadLaterRepository
import com.sorrowblue.comicviewer.domain.usecase.paging.PagingReadLaterUseCase
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

internal class PagingReadLaterInteractor @Inject constructor(
    private val readLaterRepository: ReadLaterRepository
) : PagingReadLaterUseCase() {

    override fun run(request: Request): Flow<PagingData<File>> {
        return readLaterRepository.pagingDataFlow(request.pagingConfig)
    }
}
