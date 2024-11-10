package com.sorrowblue.comicviewer.domain.usecase.file

import com.sorrowblue.comicviewer.domain.model.Resource
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.usecase.UseCase

abstract class GetFileUseCase : UseCase<GetFileUseCase.Request, File, GetFileUseCase.Error>() {

    data class Request(val bookshelfId: BookshelfId, val path: String) : UseCase.Request

    enum class Error : Resource.AppError {
        NOT_FOUND,
    }
}
