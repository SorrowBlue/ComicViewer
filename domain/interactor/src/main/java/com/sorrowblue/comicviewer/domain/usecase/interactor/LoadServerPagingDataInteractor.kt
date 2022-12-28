package com.sorrowblue.comicviewer.domain.usecase.interactor

import androidx.paging.PagingData
import com.sorrowblue.comicviewer.domain.entity.ServerBookshelf
import com.sorrowblue.comicviewer.domain.repository.ServerRepository
import com.sorrowblue.comicviewer.domain.usecase.LoadServerPagingDataUseCase
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

internal class LoadServerPagingDataInteractor @Inject constructor(
    private val repository: ServerRepository
) : LoadServerPagingDataUseCase() {

    override fun run(request: RequestData): Flow<PagingData<ServerBookshelf>> {
        return repository.pagingDataFlow(request.pagingConfig)
    }
}
