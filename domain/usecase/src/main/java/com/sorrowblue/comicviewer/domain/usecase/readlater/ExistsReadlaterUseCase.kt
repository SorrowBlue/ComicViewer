package com.sorrowblue.comicviewer.domain.usecase.readlater

import com.sorrowblue.comicviewer.domain.model.Resource
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.usecase.UseCase

abstract class ExistsReadlaterUseCase :
    UseCase<ExistsReadlaterUseCase.Request, Boolean, Resource.ReportedSystemError>() {

    class Request(val bookshelfId: BookshelfId, val path: String) : UseCase.Request
}
