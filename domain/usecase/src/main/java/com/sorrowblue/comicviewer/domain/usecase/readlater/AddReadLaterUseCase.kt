package com.sorrowblue.comicviewer.domain.usecase.readlater

import com.sorrowblue.comicviewer.domain.model.ReadLaterFile
import com.sorrowblue.comicviewer.domain.model.Resource
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.usecase.UseCase

abstract class AddReadLaterUseCase :
    UseCase<AddReadLaterUseCase.Request, ReadLaterFile, Resource.ReportedSystemError>() {

    class Request(val bookshelfId: BookshelfId, val path: String) : UseCase.Request
}
