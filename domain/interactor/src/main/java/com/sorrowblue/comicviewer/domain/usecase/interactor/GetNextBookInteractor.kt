package com.sorrowblue.comicviewer.domain.usecase.interactor

import com.sorrowblue.comicviewer.domain.entity.file.Book
import com.sorrowblue.comicviewer.domain.entity.file.Bookshelf
import com.sorrowblue.comicviewer.domain.repository.FileRepository
import com.sorrowblue.comicviewer.domain.usecase.GetLibraryInfoError
import com.sorrowblue.comicviewer.domain.usecase.GetNextBookUseCase
import com.sorrowblue.comicviewer.domain.usecase.GetNextComicRel
import com.sorrowblue.comicviewer.framework.Result
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal class GetNextBookInteractor @Inject constructor(
    private val fileRepository: FileRepository
) : GetNextBookUseCase() {

    override fun run(request: Request): Flow<Result<Book, GetLibraryInfoError>> {
        return fileRepository.getNextRelFile(
            request.serverId,
            request.path,
            request.relation == GetNextComicRel.NEXT
        ).map { result ->
            result.fold({
                when (it) {
                    is Bookshelf -> Result.Error(GetLibraryInfoError.NOT_FOUND)
                    is Book -> Result.Success(it)
                }
            }, {
                Result.Error(GetLibraryInfoError.NOT_FOUND)
            }, {
                Result.Error(GetLibraryInfoError.SYSTEM_ERROR)
            })
        }
    }
}
