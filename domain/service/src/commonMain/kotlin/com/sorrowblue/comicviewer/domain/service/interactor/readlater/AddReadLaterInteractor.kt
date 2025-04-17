package com.sorrowblue.comicviewer.domain.service.interactor.readlater

import com.sorrowblue.comicviewer.domain.model.Resource
import com.sorrowblue.comicviewer.domain.service.datasource.ReadLaterFileLocalDataSource
import com.sorrowblue.comicviewer.domain.usecase.SendFatalErrorUseCase
import com.sorrowblue.comicviewer.domain.usecase.readlater.AddReadLaterUseCase
import org.koin.core.annotation.Singleton

@Singleton
internal class AddReadLaterInteractor(
    private val localDataSource: ReadLaterFileLocalDataSource,
    private val sendFatalErrorUseCase: SendFatalErrorUseCase,
) : AddReadLaterUseCase() {

    override suspend fun run(request: Request) =
        when (val result = localDataSource.updateOrAdd(request.readLaterFile)) {
            is Resource.Success -> Resource.Success(request.readLaterFile)
            is Resource.Error -> {
                sendFatalErrorUseCase(SendFatalErrorUseCase.Request(result.error.throwable))
                Resource.Error(Unit)
            }
        }
}
