package com.sorrowblue.comicviewer.domain.service.interactor.readlater

import com.sorrowblue.comicviewer.domain.model.ReadLaterFile
import com.sorrowblue.comicviewer.domain.model.Resource
import com.sorrowblue.comicviewer.domain.service.datasource.ReadLaterFileLocalDataSource
import com.sorrowblue.comicviewer.domain.usecase.readlater.DeleteReadLaterUseCase
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

internal class DeleteReadLaterInteractor @Inject constructor(
    private val readLaterFileLocalDataSource: ReadLaterFileLocalDataSource,
) : DeleteReadLaterUseCase() {

    override fun run(request: Request): Flow<Resource<Unit, Resource.ReportedSystemError>> {
        return flow {
            when (
                val result = readLaterFileLocalDataSource.delete(
                    ReadLaterFile(
                        request.bookshelfId,
                        request.path
                    )
                )
            ) {
                is Resource.Success -> emit(Resource.Success(Unit))
                is Resource.Error -> {
                    // TODO("ReportError")
                    result.error
                    emit(Resource.Error(Resource.ReportedSystemError))
                }
            }
        }
    }
}
