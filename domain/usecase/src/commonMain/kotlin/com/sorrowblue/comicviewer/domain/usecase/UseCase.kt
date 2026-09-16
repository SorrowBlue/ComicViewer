/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.usecase

import com.sorrowblue.comicviewer.domain.model.common.Resource
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
