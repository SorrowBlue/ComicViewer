package com.sorrowblue.comicviewer.domain.interactor.bookshelf

import com.sorrowblue.comicviewer.domain.model.BookshelfFolder
import com.sorrowblue.comicviewer.domain.model.file.IFolder
import com.sorrowblue.comicviewer.domain.repository.BookshelfRepository
import com.sorrowblue.comicviewer.domain.repository.FileRepository
import com.sorrowblue.comicviewer.domain.usecase.GetLibraryInfoError
import com.sorrowblue.comicviewer.domain.usecase.bookshelf.DeleteHistoryUseCase
import com.sorrowblue.comicviewer.domain.usecase.bookshelf.GetBookshelfFolderUseCase
import com.sorrowblue.comicviewer.framework.Result
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

internal class GetBookshelfFolderInteractor @Inject constructor(
    private val bookshelfRepository: BookshelfRepository,
    private val fileRepository: FileRepository
) : GetBookshelfFolderUseCase() {

    override fun run(request: Request): Flow<Result<BookshelfFolder, GetLibraryInfoError>> {
        return bookshelfRepository.get(request.bookshelfId).map { result ->
            result.fold({ server ->
                fileRepository.get2(server.id, request.path).fold({
                    Result.Success(BookshelfFolder(server to it as IFolder))
                }, {
                    Result.Error(GetLibraryInfoError.NOT_FOUND)
                }, {
                    Result.Error(GetLibraryInfoError.NOT_FOUND)
                })
            }, {
                Result.Error(GetLibraryInfoError.NOT_FOUND)
            }, {
                Result.Error(GetLibraryInfoError.SYSTEM_ERROR)
            })

        }
    }
}

internal class DeleteHistoryInteractor @Inject constructor(
    private val fileRepository: FileRepository
) : DeleteHistoryUseCase() {

    override fun run(request: Request): Flow<Result<Unit, GetLibraryInfoError>> {
        return flow {
            emit(Result.Success(fileRepository.deleteHistory(request.bookshelfId, request.list)))
        }
    }
}
