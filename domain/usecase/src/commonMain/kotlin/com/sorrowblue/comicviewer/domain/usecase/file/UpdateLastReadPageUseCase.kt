package com.sorrowblue.comicviewer.domain.usecase.file

import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.usecase.UseCase
import kotlinx.datetime.Clock

abstract class UpdateLastReadPageUseCase :
    UseCase<UpdateLastReadPageUseCase.Request, Unit, Unit>() {

    class Request(
        val bookshelfId: BookshelfId,
        val path: String,
        val lastReadPage: Int,
        val timestamp: Long = Clock.System.now().toEpochMilliseconds(),
    ) : UseCase.Request
}
