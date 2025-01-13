package com.sorrowblue.comicviewer.domain.usecase.bookshelf

import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.usecase.OneShotUseCase

abstract class UpdateDeletionFlagUseCase :
    OneShotUseCase<UpdateDeletionFlagUseCase.Request, Unit, Unit>() {

    class Request(val bookshelfId: BookshelfId, val deleted: Boolean) : OneShotUseCase.Request
}
