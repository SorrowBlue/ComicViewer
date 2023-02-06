package com.sorrowblue.comicviewer.domain.interactor.bookshelf

import com.sorrowblue.comicviewer.domain.entity.BookshelfFile
import com.sorrowblue.comicviewer.domain.repository.FileRepository
import com.sorrowblue.comicviewer.domain.repository.BookshelfRepository
import com.sorrowblue.comicviewer.domain.usecase.GetLibraryFileResult
import com.sorrowblue.comicviewer.domain.usecase.bookshelf.GetBookshelfFileUseCase
import com.sorrowblue.comicviewer.framework.Result
import com.sorrowblue.comicviewer.framework.Unknown
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal class GetBookshelfFileInteractor @Inject constructor(
    private val bookshelfRepository: BookshelfRepository,
    private val fileRepository: FileRepository,
) : GetBookshelfFileUseCase() {

    override fun run(request: Request): Flow<Result<BookshelfFile, GetLibraryFileResult>> {
        return bookshelfRepository.get(request.bookshelfId).map { result ->
            result.fold({ server ->
                fileRepository.get(server.id, request.path).fold({ file ->
                    if (file != null) {
                        Result.Success(BookshelfFile(server to file))
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
