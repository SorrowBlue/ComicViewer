package com.sorrowblue.comicviewer.domain.service.interactor

import com.sorrowblue.comicviewer.domain.service.repository.FileRepository
import com.sorrowblue.comicviewer.domain.usecase.ScanBookshelfUseCase
import com.sorrowblue.comicviewer.framework.Result
import javax.inject.Inject

internal class ScanBookshelfInteractor @Inject constructor(
    private val fileRepository: FileRepository,
) : ScanBookshelfUseCase() {
    override suspend fun run(request: Request): Result<String, Unit> {
        return Result.Success(fileRepository.scan(request.folder, request.scan))
    }
}
