package com.sorrowblue.comicviewer.domain.usecase

import com.sorrowblue.comicviewer.domain.model.BaseRequest
import com.sorrowblue.comicviewer.domain.model.Response

abstract class OneTimeUseCase<R : BaseRequest, T> {

    fun execute(request: R): Response<T> {
        val validated = request.validate()
        return if (validated) run(request) else Response.Error(IllegalArgumentException())
    }

    protected abstract fun run(request: R): Response<T>
}

abstract class OneShotUseCase<R : BaseRequest, T> {

    fun execute(request: R): T {
        val validated = request.validate()
        return if (validated) run(request) else throw IllegalArgumentException()
    }

    protected abstract fun run(request: R): T
}
