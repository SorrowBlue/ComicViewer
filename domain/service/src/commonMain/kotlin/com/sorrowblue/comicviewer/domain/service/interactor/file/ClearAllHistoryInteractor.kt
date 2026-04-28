package com.sorrowblue.comicviewer.domain.service.interactor.file

import com.sorrowblue.comicviewer.domain.model.Resource
import com.sorrowblue.comicviewer.domain.service.datasource.FileLocalDataSource
import com.sorrowblue.comicviewer.domain.usecase.file.ClearAllHistoryUseCase
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding

@ContributesBinding(AppScope::class)
internal class ClearAllHistoryInteractor(private val fileLocalDataSource: FileLocalDataSource) :
    ClearAllHistoryUseCase() {
    override suspend fun run(request: Request): Resource<Unit, Unit> {
        fileLocalDataSource.deleteAllHistory()
        return Resource.Success(Unit)
    }
}
