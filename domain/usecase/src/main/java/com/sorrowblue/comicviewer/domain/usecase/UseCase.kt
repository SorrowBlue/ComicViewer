package com.sorrowblue.comicviewer.domain.usecase

import com.sorrowblue.comicviewer.domain.model.Resource
import kotlinx.coroutines.flow.Flow

abstract class UseCase<in R : UseCase.Request, out D, out E : Resource.AppError> {

    interface Request

    fun execute(request: R): Flow<Resource<D, E>> {
        return run(request)
    }

    protected abstract fun run(request: R): Flow<Resource<D, E>>
}
