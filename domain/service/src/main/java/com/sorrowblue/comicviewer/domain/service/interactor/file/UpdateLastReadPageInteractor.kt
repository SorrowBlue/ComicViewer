package com.sorrowblue.comicviewer.domain.service.interactor.file

import com.sorrowblue.comicviewer.domain.model.Result
import com.sorrowblue.comicviewer.domain.service.datasource.FileLocalDataSource
import com.sorrowblue.comicviewer.domain.usecase.file.UpdateLastReadPageUseCase
import java.time.ZoneOffset
import javax.inject.Inject

internal class UpdateLastReadPageInteractor @Inject constructor(
    private val fileLocalDataSource: FileLocalDataSource,
) : UpdateLastReadPageUseCase() {

    override suspend fun run(request: Request): Result<Unit, Unit> {
        fileLocalDataSource.updateHistory(
            request.path,
            request.bookshelfId,
            request.lastReadPage,
            request.timestamp.toEpochSecond(ZoneOffset.UTC) * 1000
        )
        return Result.Success(Unit)
    }
}
