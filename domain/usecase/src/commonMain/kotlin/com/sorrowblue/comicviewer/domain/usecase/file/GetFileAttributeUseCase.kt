package com.sorrowblue.comicviewer.domain.usecase.file

import com.sorrowblue.comicviewer.domain.model.Resource
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.file.FileAttribute
import com.sorrowblue.comicviewer.domain.usecase.UseCase

abstract class GetFileAttributeUseCase :
    UseCase<GetFileAttributeUseCase.Request, FileAttribute, GetFileAttributeUseCase.Error>() {

    sealed interface Error : Resource.AppError {
        data object NotFound : Error
        data object System : Error
    }

    class Request(val bookshelfId: BookshelfId, val path: String) : UseCase.Request
}
