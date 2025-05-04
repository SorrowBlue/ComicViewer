package com.sorrowblue.comicviewer.domain.usecase.bookshelf

import com.sorrowblue.comicviewer.domain.model.Resource
import com.sorrowblue.comicviewer.domain.model.bookshelf.Bookshelf
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.usecase.OneShotUseCase

abstract class RegenerateThumbnailsUseCase2 :
    OneShotUseCase<RegenerateThumbnailsUseCase2.Request, Unit, RegenerateThumbnailsUseCase2.Error>() {

    class Request(
        val bookshelfId: BookshelfId,
        val process: suspend (Bookshelf, progress: Long, max: Long) -> Unit,
    ) : OneShotUseCase.Request

    enum class Error : Resource.AppError {
        System,
    }
}
