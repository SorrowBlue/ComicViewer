package com.sorrowblue.comicviewer.domain.service.interactor

import com.sorrowblue.comicviewer.domain.model.Resource
import com.sorrowblue.comicviewer.domain.usecase.SendFatalErrorUseCase
import logcat.LogPriority
import logcat.asLog
import logcat.logcat
import jakarta.inject.Singleton

@Singleton
internal class SendFatalErrorInteractor : SendFatalErrorUseCase() {

    override suspend fun run(request: Request): Resource<Unit, Unit> {
        logcat(LogPriority.ERROR, "SendFatalErrorUseCase") { request.throwable.asLog() }
        return Resource.Success(Unit)
    }
}
