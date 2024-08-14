package com.sorrowblue.comicviewer.domain.service.interactor.readlater

import com.sorrowblue.comicviewer.domain.model.Resource
import com.sorrowblue.comicviewer.domain.service.datasource.ReadLaterFileLocalDataSource
import com.sorrowblue.comicviewer.domain.usecase.readlater.DeleteAllReadLaterUseCase
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

internal class DeleteAllReadLaterInteractor @Inject constructor(
    private val readLaterFileLocalDataSource: ReadLaterFileLocalDataSource,
) : DeleteAllReadLaterUseCase() {

    override fun run(request: Request): Flow<Resource<Unit, Error>> {
        return flow<Resource<Unit, Error>> {
            readLaterFileLocalDataSource.deleteAll()
            emit(Resource.Success(Unit))
        }.catch {
            emit(Resource.Error(Error.System))
        }
    }
}
