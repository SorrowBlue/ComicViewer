package com.sorrowblue.comicviewer.domain.usecase.bookshelf

import com.sorrowblue.comicviewer.domain.entity.bookshelf.Bookshelf
import com.sorrowblue.comicviewer.domain.request.BaseRequest
import com.sorrowblue.comicviewer.domain.usecase.FlowOneUseCase
import com.sorrowblue.comicviewer.domain.usecase.UseCaseError

abstract class RegisterBookshelfUseCase :
    FlowOneUseCase<RegisterBookshelfUseCase.Request, Bookshelf, RegisterBookshelfError>() {

    class Request(val bookshelf: Bookshelf, val path: String) : BaseRequest {
        override fun validate(): Boolean {
            return true
        }
    }
}

sealed class RegisterBookshelfError : UseCaseError {
    data object InvalidBookshelfInfo : RegisterBookshelfError()
    data object InvalidAuth : RegisterBookshelfError()
    data object InvalidPath : RegisterBookshelfError()
}
