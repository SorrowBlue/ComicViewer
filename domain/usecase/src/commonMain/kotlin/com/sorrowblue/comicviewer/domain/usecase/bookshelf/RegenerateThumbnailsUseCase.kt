package com.sorrowblue.comicviewer.domain.usecase.bookshelf

import com.sorrowblue.comicviewer.domain.model.Resource
import com.sorrowblue.comicviewer.domain.model.bookshelf.Bookshelf
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.usecase.UseCase

abstract class RegenerateThumbnailsUseCase :
    UseCase<RegenerateThumbnailsUseCase.Request, Unit, RegenerateThumbnailsUseCase.Error>() {

    class Request(
        val bookshelfId: BookshelfId,
        val process: suspend (Bookshelf, progress: Long, max: Long) -> Unit,
    ) : UseCase.Request

    enum class Error : Resource.AppError {
        System,
    }
}
