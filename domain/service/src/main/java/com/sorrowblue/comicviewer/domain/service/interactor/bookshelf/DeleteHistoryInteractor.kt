package com.sorrowblue.comicviewer.domain.service.interactor.bookshelf

import com.sorrowblue.comicviewer.domain.model.Result
import com.sorrowblue.comicviewer.domain.service.repository.FileRepository
import com.sorrowblue.comicviewer.domain.usecase.GetLibraryInfoError
import com.sorrowblue.comicviewer.domain.usecase.bookshelf.DeleteHistoryUseCase
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

internal class DeleteHistoryInteractor @Inject constructor(
    private val fileRepository: FileRepository,
) : DeleteHistoryUseCase() {

    override fun run(request: Request): Flow<Result<Unit, GetLibraryInfoError>> {
        return flow {
            emit(Result.Success(fileRepository.deleteHistory(request.bookshelfId, request.list)))
        }
    }
}
