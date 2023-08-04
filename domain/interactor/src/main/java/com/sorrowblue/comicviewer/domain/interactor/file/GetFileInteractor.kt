package com.sorrowblue.comicviewer.domain.interactor.file

import com.sorrowblue.comicviewer.domain.entity.file.File
import com.sorrowblue.comicviewer.domain.repository.FileRepository
import com.sorrowblue.comicviewer.domain.usecase.file.GetFileUseCase
import com.sorrowblue.comicviewer.framework.Resource
import com.sorrowblue.comicviewer.framework.fold
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal class GetFileInteractor @Inject constructor(
    private val fileRepository: FileRepository,
) : GetFileUseCase() {

    override fun run(request: Request): Flow<Resource<File, Error>> {
        return fileRepository.find(request.bookshelfId, request.path).map {
            it.fold({
                Resource.Success(it)
            }, {
                when (it) {
                    FileRepository.Error.System -> Resource.Error(Error.NOT_FOUND)
                }
            })
        }
    }
}
