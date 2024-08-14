package com.sorrowblue.comicviewer.domain.service.interactor.readlater

import com.sorrowblue.comicviewer.domain.model.ReadLaterFile
import com.sorrowblue.comicviewer.domain.model.Resource
import com.sorrowblue.comicviewer.domain.service.datasource.ReadLaterFileLocalDataSource
import com.sorrowblue.comicviewer.domain.usecase.SendFatalErrorUseCase
import com.sorrowblue.comicviewer.domain.usecase.readlater.AddReadLaterUseCase
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow

internal class AddReadLaterInteractor @Inject constructor(
    private val localDataSource: ReadLaterFileLocalDataSource,
    private val sendFatalErrorUseCase: SendFatalErrorUseCase,
) : AddReadLaterUseCase() {

    override fun run(request: Request): Flow<Resource<ReadLaterFile, Resource.ReportedSystemError>> {
        return flow {
            val model = ReadLaterFile(request.bookshelfId, request.path)
            when (val result = localDataSource.updateOrAdd(model)) {
                is Resource.Success -> emit(Resource.Success(model))
                is Resource.Error -> {
                    sendFatalErrorUseCase(SendFatalErrorUseCase.Request(result.error.throwable)).first()
                    emit(Resource.Error(Resource.ReportedSystemError))
                }
            }
        }
    }
}

internal class SendFatalErrorInteractor @Inject constructor() : SendFatalErrorUseCase() {
    override fun run(request: Request): Flow<Resource<Unit, Error>> {
        TODO("Not yet implemented")
    }
}
