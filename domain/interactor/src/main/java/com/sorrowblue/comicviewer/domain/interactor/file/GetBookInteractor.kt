package com.sorrowblue.comicviewer.domain.interactor.file

import com.sorrowblue.comicviewer.domain.entity.file.Book
import com.sorrowblue.comicviewer.domain.repository.FileRepository
import com.sorrowblue.comicviewer.domain.usecase.file.GetBookUseCase
import com.sorrowblue.comicviewer.framework.Result
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal class GetBookInteractor @Inject constructor(
    private val fileRepository: FileRepository
) : GetBookUseCase() {

    override fun run(request: Request): Flow<Result<Book, Unit>> {
        return fileRepository.getFile(request.bookshelfId, request.path).map { result ->
            result.fold({
                if (it is Book) {
                    Result.Success(it)
                } else {
                    Result.Error(Unit)
                }
            }, {
                Result.Error(Unit)
            }, {
                Result.Exception(it)
            })
        }
    }
}
