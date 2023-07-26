package com.sorrowblue.comicviewer.domain.usecase

import com.sorrowblue.comicviewer.domain.request.BaseRequest
import com.sorrowblue.comicviewer.framework.IllegalArguments
import com.sorrowblue.comicviewer.framework.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

abstract class FlowOneUseCase<R : BaseRequest, S, E> {

    fun execute(request: R): Flow<Result<S, E>> {
        return flow {
            emit(run(request))
        }
    }

    protected abstract suspend fun run(request: R): Result<S, E>
}
