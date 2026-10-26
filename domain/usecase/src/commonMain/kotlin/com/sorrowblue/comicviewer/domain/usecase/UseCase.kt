/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.usecase

import com.sorrowblue.comicviewer.domain.model.common.Resource
import kotlin.coroutines.cancellation.CancellationException
import kotlinx.coroutines.flow.Flow
import logcat.logcat

abstract class UseCase<in R : Any, out D, out E> {

    operator fun invoke(request: R): Flow<Resource<D, E>> {
        logcat { "#invoke request: $request" }
        return run(request)
    }

    protected abstract fun run(request: R): Flow<Resource<D, E>>
}

operator fun <D, E> UseCase<Unit, D, E>.invoke(): Flow<Resource<D, E>> = invoke(Unit)

/**
 * Calls the specified function [block] and returns its encapsulated result if invocation was
 * successful, catching any [Throwable] exception that was thrown from the [block] function
 * execution and encapsulating it as a failure, while rethrowing [CancellationException] to preserve
 * structured concurrency.
 */
internal inline fun <R> runCatchingCancellable(block: () -> R): Result<R> =
    runCatching(block).onFailure {
        if (it is CancellationException) throw it
    }
