package com.sorrowblue.comicviewer.domain.usecase.interactor

import com.sorrowblue.comicviewer.domain.entity.Book
import com.sorrowblue.comicviewer.domain.entity.Bookshelf
import com.sorrowblue.comicviewer.domain.model.Result
import com.sorrowblue.comicviewer.domain.repository.FileRepository
import com.sorrowblue.comicviewer.domain.usecase.GetLibraryInfoError
import com.sorrowblue.comicviewer.domain.usecase.GetNextComicRel
import com.sorrowblue.comicviewer.domain.usecase.GetNextComicRequest
import com.sorrowblue.comicviewer.domain.usecase.GetNextComicUseCase
import javax.inject.Inject

internal class GetNextComicInteractor @Inject constructor(
    private val fileRepository: FileRepository,
) : GetNextComicUseCase() {

    override suspend fun run(request: GetNextComicRequest): Result<Book, GetLibraryInfoError> {
        return fileRepository.getNextRelFile(
            request.serverId,
            request.path,
            request.relation == GetNextComicRel.NEXT
        ).fold({
            when (it) {
                is Bookshelf -> Result.Error(GetLibraryInfoError.NOT_FOUND)
                is Book -> Result.Success(it)
                null -> Result.Error(GetLibraryInfoError.NOT_FOUND)
            }
        }, {
            Result.Error(GetLibraryInfoError.NOT_FOUND)
        }, {
            Result.Error(GetLibraryInfoError.SYSTEM_ERROR)
        })
    }
}
