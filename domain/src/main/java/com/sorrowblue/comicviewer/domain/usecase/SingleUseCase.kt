package com.sorrowblue.comicviewer.domain.usecase

import com.sorrowblue.comicviewer.domain.model.BaseRequest
import com.sorrowblue.comicviewer.domain.model.Response
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

abstract class SingleUseCase<R : BaseRequest, T> {

    private val result = MutableSharedFlow<Response<T>>(0, 1, BufferOverflow.DROP_OLDEST)
    val source: SharedFlow<Response<T>> = result.asSharedFlow()

    suspend fun execute(request: R): Response<T> {
        val validated = request.validate()
        return if (validated) run(request) else Response.Error(IllegalArgumentException())
    }

    protected abstract suspend fun run(request: R): Response<T>
}
