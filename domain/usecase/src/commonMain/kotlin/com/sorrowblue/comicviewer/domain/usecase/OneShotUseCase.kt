/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.usecase

import com.sorrowblue.comicviewer.domain.model.common.Resource
import logcat.logcat

abstract class OneShotUseCase<in R : Any, out D, out E> {

    suspend operator fun invoke(request: R): Resource<D, E> {
        logcat { "invoke(request: $request)" }
        return run(request)
    }

    protected abstract suspend fun run(request: R): Resource<D, E>
}

suspend operator fun <D, E> OneShotUseCase<Unit, D, E>.invoke(): Resource<D, E> = invoke(Unit)
