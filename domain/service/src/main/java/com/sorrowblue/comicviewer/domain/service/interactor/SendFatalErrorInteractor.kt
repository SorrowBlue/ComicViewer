package com.sorrowblue.comicviewer.domain.service.interactor

import com.sorrowblue.comicviewer.domain.model.Resource
import com.sorrowblue.comicviewer.domain.usecase.SendFatalErrorUseCase
import javax.inject.Inject
import logcat.LogPriority
import logcat.asLog
import logcat.logcat

internal class SendFatalErrorInteractor @Inject constructor() : SendFatalErrorUseCase() {

    override suspend fun run(request: Request): Resource<Unit, Unit> {
        logcat(LogPriority.ERROR, "SendFatalErrorUseCase") { request.throwable.asLog() }
        return Resource.Success(Unit)
    }
}
