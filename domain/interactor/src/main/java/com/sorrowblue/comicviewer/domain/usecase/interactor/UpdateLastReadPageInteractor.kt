package com.sorrowblue.comicviewer.domain.usecase.interactor

import com.sorrowblue.comicviewer.framework.Result
import com.sorrowblue.comicviewer.domain.repository.FileRepository
import com.sorrowblue.comicviewer.domain.usecase.UpdateLastReadPageUseCase
import java.time.ZoneOffset
import javax.inject.Inject

internal class UpdateLastReadPageInteractor @Inject constructor(
    private val fileRepository: FileRepository
) : UpdateLastReadPageUseCase() {

    override suspend fun run(request: Request): Result<Unit, Unit> {
        fileRepository.update(
            request.libraryId,
            request.path,
            request.lastReadPage,
            request.timestamp.toEpochSecond(ZoneOffset.UTC) * 1000
        )
        return Result.Success(Unit)
    }
}
