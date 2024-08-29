package com.sorrowblue.comicviewer.domain.service.interactor.readlater

import com.sorrowblue.comicviewer.domain.model.Resource
import com.sorrowblue.comicviewer.domain.model.fold
import com.sorrowblue.comicviewer.domain.service.datasource.ReadLaterFileLocalDataSource
import com.sorrowblue.comicviewer.domain.service.interactor.SendFatalErrorInteractor
import com.sorrowblue.comicviewer.domain.usecase.SendFatalErrorUseCase
import com.sorrowblue.comicviewer.domain.usecase.readlater.DeleteAllReadLaterUseCase
import javax.inject.Inject

internal class DeleteAllReadLaterInteractor @Inject constructor(
    private val readLaterFileLocalDataSource: ReadLaterFileLocalDataSource,
    private val sendFatalErrorInteractor: SendFatalErrorInteractor,
) : DeleteAllReadLaterUseCase() {

    override suspend fun run(request: Request): Resource<Unit, Unit> {
        return readLaterFileLocalDataSource.deleteAll().fold(
            onSuccess = { Resource.Success(Unit) },
            onError = {
                sendFatalErrorInteractor(SendFatalErrorUseCase.Request(it.throwable))
                Resource.Error(Unit)
            }
        )
    }
}
