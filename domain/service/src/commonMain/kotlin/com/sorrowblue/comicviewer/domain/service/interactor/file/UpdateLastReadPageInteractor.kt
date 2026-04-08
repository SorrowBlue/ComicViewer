package com.sorrowblue.comicviewer.domain.service.interactor.file

import com.sorrowblue.comicviewer.domain.model.Resource
import com.sorrowblue.comicviewer.domain.service.datasource.FileLocalDataSource
import com.sorrowblue.comicviewer.domain.usecase.file.UpdateLastReadPageUseCase
import com.sorrowblue.comicviewer.framework.common.scope.DataScope
import dev.zacsweers.metro.ContributesBinding

@ContributesBinding(DataScope::class)
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
