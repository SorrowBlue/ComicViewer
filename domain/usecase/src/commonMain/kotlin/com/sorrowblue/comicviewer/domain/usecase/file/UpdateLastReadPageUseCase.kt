package com.sorrowblue.comicviewer.domain.usecase.file

import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.usecase.OneShotUseCase
import kotlinx.datetime.Clock

abstract class UpdateLastReadPageUseCase :
    OneShotUseCase<UpdateLastReadPageUseCase.Request, Unit, Unit>() {

    class Request(
        val bookshelfId: BookshelfId,
        val path: String,
        val lastReadPage: Int,
        val timestamp: Long = Clock.System.now().toEpochMilliseconds(),
    ) : OneShotUseCase.Request
}
