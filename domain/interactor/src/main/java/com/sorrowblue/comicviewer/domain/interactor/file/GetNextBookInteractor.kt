package com.sorrowblue.comicviewer.domain.interactor.file

import com.sorrowblue.comicviewer.domain.entity.file.Book
import com.sorrowblue.comicviewer.domain.entity.file.Folder
import com.sorrowblue.comicviewer.domain.repository.FileRepository
import com.sorrowblue.comicviewer.domain.usecase.GetLibraryInfoError
import com.sorrowblue.comicviewer.domain.usecase.GetNextComicRel
import com.sorrowblue.comicviewer.domain.usecase.file.GetNextBookUseCase
import com.sorrowblue.comicviewer.framework.Result
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal class GetNextBookInteractor @Inject constructor(
    private val fileRepository: FileRepository
) : GetNextBookUseCase() {

    override fun run(request: Request): Flow<Result<Book, GetLibraryInfoError>> {
        return fileRepository.getNextRelFile(
            request.bookshelfId,
            request.path,
            request.relation == GetNextComicRel.NEXT
        ).map { result ->
            result.fold({
                when (it) {
                    is Folder -> Result.Error(GetLibraryInfoError.NOT_FOUND)
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
