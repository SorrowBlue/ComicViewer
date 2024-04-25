package com.sorrowblue.comicviewer.domain.service.interactor.bookshelf

import com.sorrowblue.comicviewer.domain.model.BookshelfFile
import com.sorrowblue.comicviewer.domain.model.Result
import com.sorrowblue.comicviewer.domain.service.datasource.BookshelfLocalDataSource
import com.sorrowblue.comicviewer.domain.service.datasource.FileLocalDataSource
import com.sorrowblue.comicviewer.domain.usecase.GetLibraryFileResult
import com.sorrowblue.comicviewer.domain.usecase.bookshelf.GetBookshelfFileUseCase
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal class GetBookshelfFileInteractor @Inject constructor(
    private val localDataSource: BookshelfLocalDataSource,
    private val fileLocalDataSource: FileLocalDataSource,
) : GetBookshelfFileUseCase() {

    override fun run(request: Request): Flow<Result<BookshelfFile, GetLibraryFileResult>> {
        return localDataSource.flow(request.bookshelfId).map { bookshelf ->
            if (bookshelf != null) {
                val file = fileLocalDataSource.findBy(request.bookshelfId, request.path)
                if (file != null) {
                    Result.Success(BookshelfFile(bookshelf to file))
                } else {
                    Result.Error(GetLibraryFileResult.NO_FILE)
                }
            } else {
                Result.Error(GetLibraryFileResult.NO_LIBRARY)
            }
        }
    }
}
