package com.sorrowblue.comicviewer.domain.service.interactor.readlater

import com.sorrowblue.comicviewer.domain.model.Resource
import com.sorrowblue.comicviewer.domain.model.fold
import com.sorrowblue.comicviewer.domain.service.datasource.ReadLaterFileLocalDataSource
import com.sorrowblue.comicviewer.domain.usecase.SendFatalErrorUseCase
import com.sorrowblue.comicviewer.domain.usecase.readlater.DeleteAllReadLaterUseCase
import com.sorrowblue.comicviewer.framework.common.scope.DataScope
import dev.zacsweers.metro.ContributesBinding

@ContributesBinding(DataScope::class)
internal class DeleteAllReadLaterInteractor(
    private val readLaterFileLocalDataSource: ReadLaterFileLocalDataSource,
    private val sendFatalErrorInteractor: SendFatalErrorUseCase,
) : DeleteAllReadLaterUseCase() {
    override suspend fun run(request: Request): Resource<Unit, Unit> =
        readLaterFileLocalDataSource.deleteAll().fold(
            onSuccess = { Resource.Success(Unit) },
            onError = {
                sendFatalErrorInteractor(SendFatalErrorUseCase.Request(it.throwable))
                Resource.Error(Unit)
            },
        )
}
