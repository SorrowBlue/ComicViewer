package com.sorrowblue.comicviewer.domain.usecase.bookshelf

import com.sorrowblue.comicviewer.domain.BaseRequest
import com.sorrowblue.comicviewer.domain.model.BookshelfFile
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.usecase.FlowUseCase2
import com.sorrowblue.comicviewer.domain.usecase.GetLibraryFileResult

abstract class GetBookshelfFileUseCase :
    FlowUseCase2<GetBookshelfFileUseCase.Request, BookshelfFile, GetLibraryFileResult>() {

    class Request(val bookshelfId: BookshelfId, val path: String) : BaseRequest
}
