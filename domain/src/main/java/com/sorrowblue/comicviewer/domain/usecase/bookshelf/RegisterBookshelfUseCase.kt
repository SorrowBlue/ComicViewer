package com.sorrowblue.comicviewer.domain.usecase.bookshelf

import com.sorrowblue.comicviewer.domain.entity.server.Bookshelf
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
    object InvalidBookshelfInfo : RegisterBookshelfError()
    object InvalidAuth : RegisterBookshelfError()
    object InvalidPath : RegisterBookshelfError()
}
