package com.sorrowblue.comicviewer.domain.interactor.bookshelf

import com.sorrowblue.comicviewer.domain.entity.BookshelfFolder
import com.sorrowblue.comicviewer.domain.entity.file.Folder
import com.sorrowblue.comicviewer.domain.repository.FileRepository
import com.sorrowblue.comicviewer.domain.repository.BookshelfRepository
import com.sorrowblue.comicviewer.domain.usecase.GetLibraryInfoError
import com.sorrowblue.comicviewer.domain.usecase.bookshelf.GetBookshelfInfoUseCase
import com.sorrowblue.comicviewer.framework.Result
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal class GetBookshelfInfoInteractor @Inject constructor(
    private val bookshelfRepository: BookshelfRepository,
    private val fileRepository: FileRepository,
) : GetBookshelfInfoUseCase() {

    override fun run(request: Request): Flow<Result<BookshelfFolder, GetLibraryInfoError>> {
        return bookshelfRepository.get(request.bookshelfId).map { result ->
            result.fold({ server ->
                fileRepository.getRoot(server.id).fold({
                    if (it is Folder) {
                        Result.Success(BookshelfFolder(server to it))
                    } else {
                        Result.Error(GetLibraryInfoError.NOT_FOUND)
                    }
                }, {
                    Result.Error(GetLibraryInfoError.NOT_FOUND)
                }, {
                    Result.Exception(it)
                })
            }, {
                Result.Error(GetLibraryInfoError.NOT_FOUND)
            }, {
                Result.Exception(it)
            })
        }
    }
}
