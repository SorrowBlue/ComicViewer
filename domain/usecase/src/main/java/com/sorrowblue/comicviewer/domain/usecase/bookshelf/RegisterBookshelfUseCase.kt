package com.sorrowblue.comicviewer.domain.usecase.bookshelf

import com.sorrowblue.comicviewer.domain.model.Resource
import com.sorrowblue.comicviewer.domain.model.bookshelf.Bookshelf
import com.sorrowblue.comicviewer.domain.usecase.UseCase

abstract class RegisterBookshelfUseCase :
    UseCase<RegisterBookshelfUseCase.Request, Bookshelf, RegisterBookshelfUseCase.Error>() {

    class Request(val bookshelf: Bookshelf, val path: String) : UseCase.Request

    sealed interface Error : Resource.AppError {
        data object Host : Error
        data object Path : Error
        data object Auth : Error
        data object Network : Error
        data object System : Error
    }
}
