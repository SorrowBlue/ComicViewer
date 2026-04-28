package com.sorrowblue.comicviewer.domain.service.interactor.file

import com.sorrowblue.comicviewer.domain.model.Resource
import com.sorrowblue.comicviewer.domain.service.datasource.FileLocalDataSource
import com.sorrowblue.comicviewer.domain.usecase.file.UpdateLastReadPageUseCase
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding

@ContributesBinding(AppScope::class)
internal class UpdateLastReadPageInteractor(private val fileLocalDataSource: FileLocalDataSource) :
    UpdateLastReadPageUseCase() {
    override suspend fun run(request: Request): Resource<Unit, Unit> {
        fileLocalDataSource.updateHistory(
            request.path,
            request.bookshelfId,
            request.lastReadPage,
            request.timestamp,
        )
        return Resource.Success(Unit)
    }
}
