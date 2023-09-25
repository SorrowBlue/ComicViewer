package com.sorrowblue.comicviewer.domain.usecase.file

import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.file.Book
import com.sorrowblue.comicviewer.domain.request.BaseRequest
import com.sorrowblue.comicviewer.domain.usecase.FlowUseCase2

abstract class GetBookUseCase : FlowUseCase2<GetBookUseCase.Request, Book, Unit>() {

    class Request(val bookshelfId: BookshelfId, val path: String) : BaseRequest {
    }
}
