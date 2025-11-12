package com.sorrowblue.comicviewer.domain.usecase.file

import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.usecase.OneShotUseCase
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

abstract class UpdateLastReadPageUseCase :
    OneShotUseCase<UpdateLastReadPageUseCase.Request, Unit, Unit>() {
    @OptIn(ExperimentalTime::class)
    class Request(
        val bookshelfId: BookshelfId,
        val path: String,
        val lastReadPage: Int,
        val timestamp: Long = Clock.System.now().toEpochMilliseconds(),
    ) : OneShotUseCase.Request
}
