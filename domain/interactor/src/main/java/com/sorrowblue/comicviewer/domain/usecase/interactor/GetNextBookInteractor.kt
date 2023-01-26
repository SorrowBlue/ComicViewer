package com.sorrowblue.comicviewer.domain.usecase.interactor

import com.sorrowblue.comicviewer.domain.entity.file.Book
import com.sorrowblue.comicviewer.domain.entity.file.Bookshelf
import com.sorrowblue.comicviewer.framework.Result
import com.sorrowblue.comicviewer.domain.repository.FileRepository
import com.sorrowblue.comicviewer.domain.usecase.GetLibraryInfoError
import com.sorrowblue.comicviewer.domain.usecase.GetNextComicRel
import com.sorrowblue.comicviewer.domain.usecase.GetNextBookUseCase
import javax.inject.Inject

internal class GetNextBookInteractor @Inject constructor(
    private val fileRepository: FileRepository,
) : GetNextBookUseCase() {

    override suspend fun run(request: Request): Result<Book, GetLibraryInfoError> {
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
