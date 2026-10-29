/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.usecase

import com.sorrowblue.comicviewer.domain.model.common.Resource
import kotlin.coroutines.cancellation.CancellationException
import kotlinx.coroutines.flow.Flow
import logcat.logcat

/**
 * Base class for reactive use cases that take a request [R] and emit [Resource] values
 * containing data [D] or an error [E] as a [Flow].
 *
 * @param R The request parameter type.
 * @param D The success data type.
 * @param E The error type.
 */
abstract class UseCase<in R : Any, out D, out E> {

    /**
     * Invokes the use case with the given [request] and returns a [Flow] of [Resource].
     *
     * @param request The input request parameters.
     * @return A [Flow] emitting [Resource] results.
     */
    operator fun invoke(request: R): Flow<Resource<D, E>> {
        logcat { "#invoke request: $request" }
        return run(request)
    }

    /**
     * Creates the underlying [Flow] of [Resource] for the given [request].
     *
     * @param request The input request parameters.
     * @return A [Flow] emitting [Resource] results.
     */
    protected abstract fun run(request: R): Flow<Resource<D, E>>
}

/**
 * Invokes a [UseCase] that requires no input parameters.
 *
 * @return A [Flow] emitting [Resource] results.
 */
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
