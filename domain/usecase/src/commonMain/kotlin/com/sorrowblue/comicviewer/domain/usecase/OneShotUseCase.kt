/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.usecase

import com.sorrowblue.comicviewer.domain.model.common.Resource
import logcat.logcat

/**
 * Base class for one-shot suspending use cases that take a request [R] and return a [Resource]
 * containing data [D] or an error [E].
 *
 * @param R The request parameter type.
 * @param D The success data type.
 * @param E The error type.
 */
abstract class OneShotUseCase<in R : Any, out D, out E> {

    /**
     * Executes the use case with the given [request].
     *
     * @param request The input request parameters.
     * @return The resulting [Resource].
     */
    suspend operator fun invoke(request: R): Resource<D, E> {
        logcat { "#invoke request: $request" }
        return run(request)
    }

    /**
     * Performs the actual business logic of the use case.
     *
     * @param request The input request parameters.
     * @return The resulting [Resource].
     */
    protected abstract suspend fun run(request: R): Resource<D, E>
}

/**
 * Executes a [OneShotUseCase] that requires no input parameters.
 *
 * @return The resulting [Resource].
 */
suspend operator fun <D, E> OneShotUseCase<Unit, D, E>.invoke(): Resource<D, E> = invoke(Unit)
