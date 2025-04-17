package com.sorrowblue.comicviewer.domain.service.interactor.readlater

import com.sorrowblue.comicviewer.domain.model.Resource
import com.sorrowblue.comicviewer.domain.service.datasource.ReadLaterFileLocalDataSource
import com.sorrowblue.comicviewer.domain.usecase.SendFatalErrorUseCase
import com.sorrowblue.comicviewer.domain.usecase.readlater.DeleteReadLaterUseCase
import org.koin.core.annotation.Singleton

@Singleton
internal class DeleteReadLaterInteractor(
    private val readLaterFileLocalDataSource: ReadLaterFileLocalDataSource,
    private val sendFatalErrorUseCase: SendFatalErrorUseCase,
) : DeleteReadLaterUseCase() {

    override suspend fun run(request: Request): Resource<Unit, Unit> {
        return when (val result = readLaterFileLocalDataSource.delete(request.readLaterFile)) {
            is Resource.Success -> Resource.Success(Unit)
            is Resource.Error -> {
                sendFatalErrorUseCase(SendFatalErrorUseCase.Request(result.error.throwable))
                Resource.Error(Unit)
            }
        }
    }
}
