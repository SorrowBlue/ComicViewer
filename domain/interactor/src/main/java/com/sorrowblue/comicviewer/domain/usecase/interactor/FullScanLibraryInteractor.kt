package com.sorrowblue.comicviewer.domain.usecase.interactor

import com.sorrowblue.comicviewer.domain.repository.FileRepository
import com.sorrowblue.comicviewer.domain.usecase.FullScanLibraryUseCase
import com.sorrowblue.comicviewer.framework.Result
import javax.inject.Inject

internal class FullScanLibraryInteractor @Inject constructor(
    private val fileRepository: FileRepository,
) : FullScanLibraryUseCase() {
    override suspend fun run(request: Request): Result<String, Unit> {
        return Result.Success(fileRepository.scan(request.folder, request.scanType))
    }
}
