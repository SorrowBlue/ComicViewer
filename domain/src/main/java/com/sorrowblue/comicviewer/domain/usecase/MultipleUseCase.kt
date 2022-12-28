package com.sorrowblue.comicviewer.domain.usecase

import com.sorrowblue.comicviewer.domain.model.BaseRequest
import com.sorrowblue.comicviewer.domain.model.IllegalArguments
import com.sorrowblue.comicviewer.domain.model.Response
import com.sorrowblue.comicviewer.domain.model.Result
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.flow

abstract class MultipleUseCase<R : BaseRequest, T> {

    private val result = MutableSharedFlow<Response<T>>(0, 1, BufferOverflow.DROP_OLDEST)
    val source: SharedFlow<Response<T>> = result.asSharedFlow()

    suspend fun execute(request: R) {
        val validated = request.validate()
        result.tryEmit(if (validated) run(request) else Response.Error(IllegalArgumentException()))
    }

    protected abstract suspend fun run(request: R): Response<T>
}

abstract class OneTimeUseCase2<R : BaseRequest, S, E> {

    suspend fun execute(request: R): Result<S, E> {
        val validated = request.validate()
        return if (validated) run(request) else Result.Exception(IllegalArguments)
    }

    protected abstract suspend fun run(request: R): Result<S, E>
}

abstract class FlowUseCase<R : BaseRequest, S, E> {

    private val result = MutableSharedFlow<Result<S, E>>(0, 1, BufferOverflow.DROP_OLDEST)
    val source: SharedFlow<Result<S, E>> = result.asSharedFlow()

    suspend fun execute(request: R) {
        val validated = request.validate()
        result.tryEmit(if (validated) run(request) else Result.Exception(IllegalArguments))
    }

    protected abstract suspend fun run(request: R): Result<S, E>
}

abstract class FlowOneUseCase<R : BaseRequest, S, E> {

    fun execute(request: R): Flow<Result<S, E>> {
        val validated = request.validate()
        return flow {
            emit(if (validated) run(request) else Result.Exception(IllegalArguments))
        }
    }

    protected abstract suspend fun run(request: R): Result<S, E>
}
