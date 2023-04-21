package com.sorrowblue.comicviewer.domain.interactor.file

import com.sorrowblue.comicviewer.domain.repository.FileRepository
import com.sorrowblue.comicviewer.domain.usecase.GetLibraryInfoError
import com.sorrowblue.comicviewer.domain.usecase.file.DeleteThumbnailsUseCase
import com.sorrowblue.comicviewer.framework.Result
import javax.inject.Inject

internal class DeleteThumbnailsInteractor @Inject constructor(
    private val fileRepository: FileRepository
) : DeleteThumbnailsUseCase() {

    override suspend fun run(request: Request): Result<Unit, GetLibraryInfoError> {
        fileRepository.deleteThumbnails()
        return Result.Success(Unit)
    }
}
