package com.sorrowblue.comicviewer.domain.usecase.paging

import androidx.paging.PagingData
import com.sorrowblue.comicviewer.domain.request.BaseRequest
import kotlinx.coroutines.flow.Flow

abstract class PagingUseCase<R : BaseRequest, S : Any> {

    fun execute(request: R): Flow<PagingData<S>> {
        return run(request)
    }

    protected abstract fun run(request: R): Flow<PagingData<S>>
}
