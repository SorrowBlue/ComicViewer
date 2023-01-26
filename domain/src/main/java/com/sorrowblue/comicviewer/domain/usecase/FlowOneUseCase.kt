package com.sorrowblue.comicviewer.domain.usecase

import com.sorrowblue.comicviewer.domain.request.BaseRequest
import com.sorrowblue.comicviewer.framework.IllegalArguments
import com.sorrowblue.comicviewer.framework.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

abstract class FlowOneUseCase<R : BaseRequest, S, E> {

    fun execute(request: R): Flow<Result<S, E>> {
        val validated = request.validate()
        return flow {
            emit(if (validated) run(request) else Result.Exception(IllegalArguments))
        }
    }

    protected abstract suspend fun run(request: R): Result<S, E>
}
