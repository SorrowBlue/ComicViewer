package com.sorrowblue.comicviewer.domain.usecase

import com.sorrowblue.comicviewer.domain.request.BaseRequest
import com.sorrowblue.comicviewer.framework.IllegalArguments
import com.sorrowblue.comicviewer.framework.Result
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.flowOf

abstract class FlowUseCase<R : BaseRequest, S, E> {

    private val result = MutableSharedFlow<Result<S, E>>(1, 1, BufferOverflow.DROP_OLDEST)
    val source: SharedFlow<Result<S, E>> = result.asSharedFlow()

    suspend fun execute(request: R) {
        val validated = request.validate()
        result.tryEmit(if (validated) run(request) else Result.Exception(IllegalArguments))
    }

    protected abstract suspend fun run(request: R): Result<S, E>
}

abstract class FlowUseCase2<R : BaseRequest, S, E> {

    fun execute(request: R): Flow<Result<S, E>> {
        val validated = request.validate()
        return if (validated) run(request) else flowOf(Result.Exception(IllegalArguments))
    }

    protected abstract fun run(request: R): Flow<Result<S, E>>
}

interface UseCaseError
