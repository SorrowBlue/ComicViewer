package com.sorrowblue.comicviewer.domain.usecase.bookshelf

import com.sorrowblue.comicviewer.domain.entity.BookshelfFolder
import com.sorrowblue.comicviewer.domain.entity.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.usecase.UseCase
import com.sorrowblue.comicviewer.framework.Resource

abstract class GetBookshelfInfoUseCase :
    UseCase<GetBookshelfInfoUseCase.Request, BookshelfFolder, GetBookshelfInfoUseCase.Error>() {

    class Request(val bookshelfId: BookshelfId) : UseCase.Request

    sealed interface Error : Resource.AppError {
        data object NotFound : Error
        data object System : Error
    }
}
