package com.sorrowblue.comicviewer.domain.usecase

import com.sorrowblue.comicviewer.domain.model.Resource
import kotlinx.coroutines.flow.Flow
import logcat.logcat

abstract class UseCase<in R : UseCase.Request, out D, out E> {

    interface Request

    operator fun invoke(request: R): Flow<Resource<D, E>> {
        logcat { "invoke(request: $request) " }
        return run(request)
    }

    protected abstract fun run(request: R): Flow<Resource<D, E>>
}

abstract class OneShotUseCase<in R : OneShotUseCase.Request, out D, out E> {

    interface Request

    suspend operator fun invoke(request: R): Resource<D, E> {
        logcat { "invoke(request: $request) " }
        return run(request)
    }

    protected abstract suspend fun run(request: R): Resource<D, E>
}
