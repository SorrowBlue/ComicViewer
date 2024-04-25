package com.sorrowblue.comicviewer.domain.service.interactor.bookshelf

import com.sorrowblue.comicviewer.domain.model.Result
import com.sorrowblue.comicviewer.domain.service.datasource.BookshelfLocalDataSource
import com.sorrowblue.comicviewer.domain.usecase.bookshelf.RemoveBookshelfUseCase
import javax.inject.Inject

internal class RemoveBookshelfInteractor @Inject constructor(
    private val bookshelfLocalDataSource: BookshelfLocalDataSource,
) : RemoveBookshelfUseCase() {

    override suspend fun run(request: Request): Result<Boolean, Unit> {
        // TODO(キャッシュファイルの削除)
        bookshelfLocalDataSource.delete(request.bookshelf)
        return Result.Success(true)
    }
}
