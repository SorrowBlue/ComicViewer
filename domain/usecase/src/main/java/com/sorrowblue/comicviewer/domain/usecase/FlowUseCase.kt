package com.sorrowblue.comicviewer.domain.usecase

import com.sorrowblue.comicviewer.domain.BaseRequest
import com.sorrowblue.comicviewer.domain.model.Result
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

abstract class FlowUseCase<R : BaseRequest, S, E> {

    private val result = MutableSharedFlow<Result<S, E>>(1, 1, BufferOverflow.DROP_OLDEST)
    val source: SharedFlow<Result<S, E>> = result.asSharedFlow()

    suspend fun execute(request: R) {
        result.tryEmit(run(request))
    }

    protected abstract suspend fun run(request: R): Result<S, E>
}

abstract class FlowUseCase2<R : BaseRequest, S, E> {

    fun execute(request: R): Flow<Result<S, E>> {
        return run(request)
    }

    protected abstract fun run(request: R): Flow<Result<S, E>>
}

