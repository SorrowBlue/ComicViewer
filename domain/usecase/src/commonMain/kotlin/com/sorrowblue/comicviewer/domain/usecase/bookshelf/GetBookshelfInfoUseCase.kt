package com.sorrowblue.comicviewer.domain.usecase.bookshelf

import com.sorrowblue.comicviewer.domain.model.BookshelfFolder
import com.sorrowblue.comicviewer.domain.model.Resource
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.usecase.UseCase

abstract class GetBookshelfInfoUseCase :
    UseCase<GetBookshelfInfoUseCase.Request, BookshelfFolder, GetBookshelfInfoUseCase.Error>() {

    class Request(val bookshelfId: BookshelfId) : UseCase.Request

    sealed interface Error : Resource.AppError {
        data object NotFound : Error
        data object System : Error
    }
}
