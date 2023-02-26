package com.sorrowblue.comicviewer.domain.interactor.bookshelf

import com.sorrowblue.comicviewer.domain.repository.BookshelfRepository
import com.sorrowblue.comicviewer.domain.usecase.bookshelf.RemoveBookshelfUseCase
import com.sorrowblue.comicviewer.framework.Result
import javax.inject.Inject

internal class RemoveBookshelfInteractor @Inject constructor(
    private val bookshelfRepository: BookshelfRepository
) : RemoveBookshelfUseCase() {

    override suspend fun run(request: Request): Result<Boolean, Unit> {
        // TODO(キャッシュファイルの削除)
        return bookshelfRepository.delete(request.bookshelf).fold({
            Result.Success(it)
        }, {
            Result.Error(Unit)
        })
    }
}
