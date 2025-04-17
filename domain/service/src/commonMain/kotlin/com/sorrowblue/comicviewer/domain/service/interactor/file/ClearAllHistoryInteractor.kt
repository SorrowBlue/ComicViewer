package com.sorrowblue.comicviewer.domain.service.interactor.file

import com.sorrowblue.comicviewer.domain.model.Resource
import com.sorrowblue.comicviewer.domain.service.datasource.FileLocalDataSource
import com.sorrowblue.comicviewer.domain.usecase.file.ClearAllHistoryUseCase
import org.koin.core.annotation.Singleton

@Singleton
internal class ClearAllHistoryInteractor(
    private val fileLocalDataSource: FileLocalDataSource,
) : ClearAllHistoryUseCase() {
    override suspend fun run(request: Request): Resource<Unit, Unit> {
        fileLocalDataSource.deleteAllHistory()
        return Resource.Success(Unit)
    }
}
