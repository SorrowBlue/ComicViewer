/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.usecase

import com.sorrowblue.comicviewer.domain.model.common.Resource
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import logcat.LogPriority
import logcat.asLog
import logcat.logcat

/**
 * Reports a fatal error for logging or crash reporting.
 */
abstract class SendFatalErrorUseCase :
    OneShotUseCase<SendFatalErrorUseCase.Request, Unit, Unit>() {

    /**
     * Request parameters for [SendFatalErrorUseCase].
     *
     * @property throwable The fatal [Throwable] to report.
     */
    data class Request(val throwable: Throwable)
}

@Inject
@ContributesBinding(AppScope::class)
internal class SendFatalErrorUseCaseImpl : SendFatalErrorUseCase() {

    override suspend fun run(request: Request): Resource<Unit, Unit> {
        logcat(LogPriority.ERROR, "SendFatalErrorUseCase") { request.throwable.asLog() }
        return Resource.Success(Unit)
    }
}
