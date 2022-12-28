package com.sorrowblue.comicviewer.domain.usecase.interactor

import com.sorrowblue.comicviewer.domain.entity.File
import com.sorrowblue.comicviewer.domain.model.Result
import com.sorrowblue.comicviewer.domain.repository.FileRepository
import com.sorrowblue.comicviewer.domain.usecase.GetFileRequest
import com.sorrowblue.comicviewer.domain.usecase.GetFileUseCase
import com.sorrowblue.comicviewer.domain.usecase.GetLibraryInfoError
import javax.inject.Inject

internal class GetFileInteractor @Inject constructor(
    private val bookshelfRepository: FileRepository,
) : GetFileUseCase() {

    override suspend fun run(request: GetFileRequest): Result<File, GetLibraryInfoError> {
        return bookshelfRepository.get(request.serverId, request.path).fold({
            Result.Success(it!!)
        }, {
            Result.Error(GetLibraryInfoError.NOT_FOUND)
        })
    }
}
