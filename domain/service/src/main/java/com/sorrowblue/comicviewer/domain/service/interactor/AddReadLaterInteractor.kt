package com.sorrowblue.comicviewer.domain.service.interactor

import com.sorrowblue.comicviewer.domain.model.Resource
import com.sorrowblue.comicviewer.domain.service.repository.FileRepository
import com.sorrowblue.comicviewer.domain.usecase.file.AddReadLaterUseCase
import com.sorrowblue.comicviewer.domain.usecase.file.DeleteAllReadLaterUseCase
import com.sorrowblue.comicviewer.domain.usecase.file.DeleteReadLaterUseCase
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal class AddReadLaterInteractor @Inject constructor(
    private val repository: FileRepository,
) : AddReadLaterUseCase() {

    override fun run(request: Request): Flow<Resource<Unit, Error>> {
        return repository.addReadLater(request.bookshelfId, request.path).mapFold({
            Resource.Success(Unit)
        }, {
            Resource.Error(Error.System)
        })
    }
}

internal class DeleteReadLaterInteractor @Inject constructor(
    private val repository: FileRepository,
) : DeleteReadLaterUseCase() {

    override fun run(request: Request): Flow<Resource<Unit, Error>> {
        return repository.deleteReadLater(request.bookshelfId, request.path).mapFold({
            Resource.Success(Unit)
        }, {
            Resource.Error(Error.System)
        })
    }
}

internal class DeleteAllReadLaterInteractor @Inject constructor(
    private val repository: FileRepository,
) : DeleteAllReadLaterUseCase() {

    override fun run(request: Request): Flow<Resource<Unit, Error>> {
        return repository.deleteAllReadLater().mapFold({
            Resource.Success(Unit)
        }, {
            Resource.Error(Error.System)
        })
    }
}

fun <D, E : Resource.AppError, R> Flow<Resource<D, E>>.mapFold(
    onSuccess: (D) -> R,
    onError: (E) -> R,
): Flow<R> {
    return map {
        when (it) {
            is Resource.Error -> onError(it.error)
            is Resource.Success -> onSuccess(it.data)
        }
    }
}
