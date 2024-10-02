package com.sorrowblue.comicviewer.domain.usecase.bookshelf

import com.sorrowblue.comicviewer.domain.model.Resource
import com.sorrowblue.comicviewer.domain.model.bookshelf.Bookshelf
import com.sorrowblue.comicviewer.domain.usecase.OneShotUseCase

abstract class RegisterBookshelfUseCase :
    OneShotUseCase<RegisterBookshelfUseCase.Request, Bookshelf, RegisterBookshelfUseCase.Error>() {

    class Request(val bookshelf: Bookshelf, val path: String) : OneShotUseCase.Request

    sealed interface Error : Resource.AppError {
        data object Host : Error
        data object Path : Error
        data object Auth : Error
        data object Network : Error
        data object System : Error
    }
}
