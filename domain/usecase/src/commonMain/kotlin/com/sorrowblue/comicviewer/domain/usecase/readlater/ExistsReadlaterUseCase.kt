package com.sorrowblue.comicviewer.domain.usecase.readlater

import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.usecase.UseCase

abstract class ExistsReadlaterUseCase :
    UseCase<ExistsReadlaterUseCase.Request, Boolean, Unit>() {

    class Request(val bookshelfId: BookshelfId, val path: String) : UseCase.Request
}
