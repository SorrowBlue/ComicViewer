package com.sorrowblue.comicviewer.domain.service.interactor.readlater

import com.sorrowblue.comicviewer.domain.model.ReadLaterFile
import com.sorrowblue.comicviewer.domain.model.Resource
import com.sorrowblue.comicviewer.domain.model.fold
import com.sorrowblue.comicviewer.domain.service.datasource.ReadLaterFileLocalDataSource
import com.sorrowblue.comicviewer.domain.usecase.SendFatalErrorUseCase
import com.sorrowblue.comicviewer.domain.usecase.readlater.ExistsReadlaterUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import org.koin.core.annotation.Factory

@Factory
internal class ExistsReadlaterInteractor(
    private val readLaterFileLocalDataSource: ReadLaterFileLocalDataSource,
    private val sendFatalErrorUseCase: SendFatalErrorUseCase,
) : ExistsReadlaterUseCase() {

    override fun run(request: Request): Flow<Resource<Boolean, Unit>> {
        return readLaterFileLocalDataSource.exists(ReadLaterFile(request.bookshelfId, request.path))
            .fold(
                onSuccess = { flow ->
                    flow.map { Resource.Success(it) }
                },
                onError = {
                    flow {
                        sendFatalErrorUseCase(SendFatalErrorUseCase.Request(it.throwable))
                        emit(Resource.Error(Unit))
                    }
                }
            )
    }
}
