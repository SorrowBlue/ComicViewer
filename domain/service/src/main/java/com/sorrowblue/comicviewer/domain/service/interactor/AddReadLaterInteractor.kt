package com.sorrowblue.comicviewer.domain.service.interactor

import com.sorrowblue.comicviewer.domain.model.ReadLaterFile
import com.sorrowblue.comicviewer.domain.model.Resource
import com.sorrowblue.comicviewer.domain.service.datasource.ReadLaterFileModelLocalDataSource
import com.sorrowblue.comicviewer.domain.usecase.file.AddReadLaterUseCase
import com.sorrowblue.comicviewer.domain.usecase.file.DeleteAllReadLaterUseCase
import com.sorrowblue.comicviewer.domain.usecase.file.DeleteReadLaterUseCase
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

internal class AddReadLaterInteractor @Inject constructor(
    private val readLaterFileModelLocalDataSource: ReadLaterFileModelLocalDataSource,
) : AddReadLaterUseCase() {

    override fun run(request: Request): Flow<Resource<Unit, Error>> {
        return flow<Resource<Unit, Error>> {
            readLaterFileModelLocalDataSource.add(ReadLaterFile(request.bookshelfId, request.path))
            emit(Resource.Success(Unit))
        }.catch {
            emit(Resource.Error(Error.System))
        }
    }
}

internal class DeleteReadLaterInteractor @Inject constructor(
    private val readLaterFileModelLocalDataSource: ReadLaterFileModelLocalDataSource,
) : DeleteReadLaterUseCase() {

    override fun run(request: Request): Flow<Resource<Unit, Error>> {
        return flow<Resource<Unit, Error>> {
            readLaterFileModelLocalDataSource.delete(
                ReadLaterFile(
                    request.bookshelfId,
                    request.path
                )
            )
            emit(Resource.Success(Unit))
        }.catch {
            emit(Resource.Error(Error.System))
        }
    }
}

internal class DeleteAllReadLaterInteractor @Inject constructor(
    private val readLaterFileModelLocalDataSource: ReadLaterFileModelLocalDataSource,
) : DeleteAllReadLaterUseCase() {

    override fun run(request: Request): Flow<Resource<Unit, Error>> {
        return flow<Resource<Unit, Error>> {
            readLaterFileModelLocalDataSource.deleteAll()
            emit(Resource.Success(Unit))
        }.catch {
            emit(Resource.Error(Error.System))
        }
    }
}
