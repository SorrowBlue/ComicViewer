package com.sorrowblue.comicviewer.domain.usecase.bookshelf

import com.sorrowblue.comicviewer.domain.model.bookshelf.Bookshelf
import com.sorrowblue.comicviewer.domain.request.BaseRequest
import com.sorrowblue.comicviewer.domain.usecase.FlowUseCase

abstract class RemoveBookshelfUseCase :
    FlowUseCase<RemoveBookshelfUseCase.Request, Boolean, Unit>() {

    class Request(val bookshelf: Bookshelf) : BaseRequest {
    }
}
