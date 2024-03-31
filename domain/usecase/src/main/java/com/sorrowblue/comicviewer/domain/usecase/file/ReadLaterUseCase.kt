package com.sorrowblue.comicviewer.domain.usecase.file

import com.sorrowblue.comicviewer.domain.model.Resource
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.usecase.UseCase

abstract class AddReadLaterUseCase :
    UseCase<AddReadLaterUseCase.Request, Unit, AddReadLaterUseCase.Error>() {

    class Request(val bookshelfId: BookshelfId, val path: String) : UseCase.Request

    enum class Error : Resource.AppError {
        System,
    }
}

abstract class DeleteReadLaterUseCase :
    UseCase<DeleteReadLaterUseCase.Request, Unit, DeleteReadLaterUseCase.Error>() {

    class Request(val bookshelfId: BookshelfId, val path: String) : UseCase.Request

    enum class Error : Resource.AppError {
        System,
    }
}

abstract class DeleteAllReadLaterUseCase :
    UseCase<DeleteAllReadLaterUseCase.Request, Unit, DeleteAllReadLaterUseCase.Error>() {

    data object Request : UseCase.Request

    enum class Error : Resource.AppError {
        System,
    }
}

abstract class ExistsReadlaterUseCase :
    UseCase<ExistsReadlaterUseCase.Request, Boolean, ExistsReadlaterUseCase.Error>() {

    class Request(val bookshelfId: BookshelfId, val path: String) : UseCase.Request

    sealed interface Error : Resource.AppError {
        data object System : Error
    }
}
