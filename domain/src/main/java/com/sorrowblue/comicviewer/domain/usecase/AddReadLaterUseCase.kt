package com.sorrowblue.comicviewer.domain.usecase

import com.sorrowblue.comicviewer.domain.entity.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.framework.Resource

abstract class AddReadLaterUseCase :
    UseCase<AddReadLaterUseCase.Request, Unit, AddReadLaterUseCase.Error>() {

    class Request(val bookshelfId: BookshelfId, val path: String) : UseCase.Request

    enum class Error : Resource.AppError {
        System
    }
}
