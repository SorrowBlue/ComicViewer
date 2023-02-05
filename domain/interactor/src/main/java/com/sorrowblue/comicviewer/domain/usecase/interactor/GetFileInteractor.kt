package com.sorrowblue.comicviewer.domain.usecase.interactor

import com.sorrowblue.comicviewer.domain.entity.file.File
import com.sorrowblue.comicviewer.domain.repository.FileRepository
import com.sorrowblue.comicviewer.domain.usecase.GetFileUseCase
import com.sorrowblue.comicviewer.domain.usecase.GetLibraryInfoError
import com.sorrowblue.comicviewer.framework.Result
import javax.inject.Inject

internal class GetFileInteractor @Inject constructor(
    private val fileRepository: FileRepository,
) : GetFileUseCase() {

    override suspend fun run(request: Request): Result<File, GetLibraryInfoError> {
        return fileRepository.get(request.serverId, request.path).fold({
            Result.Success(it!!)
        }, {
            Result.Error(GetLibraryInfoError.NOT_FOUND)
        })
    }
}
