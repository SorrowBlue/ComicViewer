package com.sorrowblue.comicviewer.domain.service.interactor.bookshelf

import com.sorrowblue.comicviewer.domain.model.BookshelfBook
import com.sorrowblue.comicviewer.domain.model.Result
import com.sorrowblue.comicviewer.domain.model.Unknown
import com.sorrowblue.comicviewer.domain.service.repository.BookshelfRepository
import com.sorrowblue.comicviewer.domain.service.repository.FileRepository
import com.sorrowblue.comicviewer.domain.usecase.GetLibraryFileResult
import com.sorrowblue.comicviewer.domain.usecase.bookshelf.GetBookshelfBookUseCase
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal class GetBookshelfBookInteractor @Inject constructor(
    private val bookshelfRepository: BookshelfRepository,
    private val fileRepository: FileRepository,
) : GetBookshelfBookUseCase() {

    override fun run(request: Request): Flow<Result<BookshelfBook, GetLibraryFileResult>> {
        return bookshelfRepository.get(request.bookshelfId).map { result ->
            result.fold({ server ->
                fileRepository.getBook(server.id, request.path).fold({ file ->
                    if (file != null) {
                        Result.Success(BookshelfBook(server to file))
                    } else {
                        Result.Error(GetLibraryFileResult.NO_FILE)
                    }
                }, {
                    Result.Exception(Unknown(it))
                })
            }, {
                Result.Error(GetLibraryFileResult.NO_LIBRARY)
            }, {
                Result.Exception(it)
            })
        }
    }
}
