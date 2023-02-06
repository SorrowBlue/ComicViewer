package com.sorrowblue.comicviewer.domain.interactor

import com.sorrowblue.comicviewer.domain.entity.ReadLaterFile
import com.sorrowblue.comicviewer.domain.repository.ReadLaterRepository
import com.sorrowblue.comicviewer.domain.usecase.AddReadLaterUseCase
import com.sorrowblue.comicviewer.framework.Result
import javax.inject.Inject

internal class AddReadLaterInteractor @Inject constructor(
    private val readLaterRepository: ReadLaterRepository
) : AddReadLaterUseCase() {

    override suspend fun run(request: Request): Result<ReadLaterFile, Unit> {
        return readLaterRepository.add(ReadLaterFile(request.bookshelfId, request.path)).fold({
            Result.Success(it)
        }, {
            Result.Error(it)
        }, { Result.Exception(it) })
    }
}
