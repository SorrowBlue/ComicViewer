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

abstract class DeleteReadLaterUseCase :
    UseCase<DeleteReadLaterUseCase.Request, Unit, DeleteReadLaterUseCase.Error>() {

    class Request(val bookshelfId: BookshelfId, val path: String) : UseCase.Request

    enum class Error : Resource.AppError {
        System
    }
}


abstract class DeleteAllReadLaterUseCase :
    UseCase<DeleteAllReadLaterUseCase.Request, Unit, DeleteAllReadLaterUseCase.Error>() {

    data object Request : UseCase.Request

    enum class Error : Resource.AppError {
        System
    }
}
