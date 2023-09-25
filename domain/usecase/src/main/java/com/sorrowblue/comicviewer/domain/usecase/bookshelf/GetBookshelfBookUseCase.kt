package com.sorrowblue.comicviewer.domain.usecase.bookshelf

import com.sorrowblue.comicviewer.domain.BaseRequest
import com.sorrowblue.comicviewer.domain.model.BookshelfBook
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.usecase.FlowUseCase2
import com.sorrowblue.comicviewer.domain.usecase.GetLibraryFileResult

abstract class GetBookshelfBookUseCase :
    FlowUseCase2<GetBookshelfBookUseCase.Request, BookshelfBook, GetLibraryFileResult>() {

    class Request(val bookshelfId: BookshelfId, val path: String) : BaseRequest
}
