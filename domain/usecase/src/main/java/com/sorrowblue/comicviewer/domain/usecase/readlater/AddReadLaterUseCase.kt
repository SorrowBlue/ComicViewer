package com.sorrowblue.comicviewer.domain.usecase.readlater

import com.sorrowblue.comicviewer.domain.model.ReadLaterFile
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.usecase.OneShotUseCase

abstract class AddReadLaterUseCase :
    OneShotUseCase<AddReadLaterUseCase.Request, ReadLaterFile, Unit>() {

    class Request private constructor(val readLaterFile: ReadLaterFile) : OneShotUseCase.Request {
        constructor(bookshelfId: BookshelfId, path: String) : this(ReadLaterFile(bookshelfId, path))
    }
}
