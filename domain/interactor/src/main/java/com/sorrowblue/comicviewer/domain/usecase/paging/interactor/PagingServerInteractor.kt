package com.sorrowblue.comicviewer.domain.usecase.paging.interactor

import androidx.paging.PagingData
import com.sorrowblue.comicviewer.domain.entity.ServerFolder
import com.sorrowblue.comicviewer.domain.repository.ServerRepository
import com.sorrowblue.comicviewer.domain.usecase.paging.PagingServerUseCase
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

internal class PagingServerInteractor @Inject constructor(
    private val repository: ServerRepository
) : PagingServerUseCase() {

    override fun run(request: Request): Flow<PagingData<ServerFolder>> {
        return repository.pagingDataFlow(request.pagingConfig)
    }
}
