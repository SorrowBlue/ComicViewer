package com.sorrowblue.comicviewer.domain.service.interactor.file

import com.sorrowblue.comicviewer.domain.model.Resource
import com.sorrowblue.comicviewer.domain.model.fold
import com.sorrowblue.comicviewer.domain.service.repository.FileRepository
import com.sorrowblue.comicviewer.domain.usecase.file.ExistsReadlaterUseCase
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal class ExistsReadlaterInteractor @Inject constructor(
    private val fileRepository: FileRepository,
) : ExistsReadlaterUseCase() {

    override fun run(request: Request): Flow<Resource<Boolean, Error>> {
        return fileRepository.existsReadLater(request.bookshelfId, request.path).map { result ->
            result.fold({ Resource.Success(it) }, { Resource.Error(Error.System) })
        }
    }
}
