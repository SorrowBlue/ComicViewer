package com.sorrowblue.comicviewer.domain.service.interactor.bookshelf

import com.sorrowblue.comicviewer.domain.model.BookshelfBook
import com.sorrowblue.comicviewer.domain.model.Result
import com.sorrowblue.comicviewer.domain.model.file.Book
import com.sorrowblue.comicviewer.domain.service.datasource.BookshelfLocalDataSource
import com.sorrowblue.comicviewer.domain.service.datasource.FileLocalDataSource
import com.sorrowblue.comicviewer.domain.usecase.GetLibraryFileResult
import com.sorrowblue.comicviewer.domain.usecase.bookshelf.GetBookshelfBookUseCase
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal class GetBookshelfBookInteractor @Inject constructor(
    private val bookshelfLocalDataSource: BookshelfLocalDataSource,
    private val fileLocalDataSource: FileLocalDataSource,
) : GetBookshelfBookUseCase() {

    override fun run(request: Request): Flow<Result<BookshelfBook, GetLibraryFileResult>> {
        return bookshelfLocalDataSource.flow(request.bookshelfId).map { bookshelf ->
            if (bookshelf != null) {
                val file = fileLocalDataSource.findBy(request.bookshelfId, request.path)
                if (file is Book) {
                    Result.Success(BookshelfBook(bookshelf to file))
                } else {
                    Result.Error(GetLibraryFileResult.NO_FILE)
                }
            } else {
                Result.Error(GetLibraryFileResult.NO_LIBRARY)
            }
        }
    }
}
