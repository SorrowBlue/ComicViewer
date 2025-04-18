package com.sorrowblue.comicviewer.domain.service.interactor.file

import com.sorrowblue.comicviewer.domain.model.Resource
import com.sorrowblue.comicviewer.domain.service.datasource.FileLocalDataSource
import com.sorrowblue.comicviewer.domain.usecase.file.UpdateLastReadPageUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.koin.core.annotation.Singleton

@Singleton
internal class UpdateLastReadPageInteractor(
    private val fileLocalDataSource: FileLocalDataSource,
) : UpdateLastReadPageUseCase() {
    override fun run(request: Request): Flow<Resource<Unit, Unit>> {
        return flow {
            fileLocalDataSource.updateHistory(
                request.path,
                request.bookshelfId,
                request.lastReadPage,
                request.timestamp
            )
            emit(Resource.Success(Unit))
        }
    }
}
