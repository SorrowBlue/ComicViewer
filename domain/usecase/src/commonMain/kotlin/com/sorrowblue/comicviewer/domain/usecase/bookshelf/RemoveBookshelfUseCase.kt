package com.sorrowblue.comicviewer.domain.usecase.bookshelf

import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.usecase.OneShotUseCase

abstract class RemoveBookshelfUseCase :
    OneShotUseCase<RemoveBookshelfUseCase.Request, Unit, Unit>() {

    class Request(val bookshelfId: BookshelfId) : OneShotUseCase.Request
}
