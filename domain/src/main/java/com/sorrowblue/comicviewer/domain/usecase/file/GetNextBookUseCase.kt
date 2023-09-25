package com.sorrowblue.comicviewer.domain.usecase.file

import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.file.Book
import com.sorrowblue.comicviewer.domain.request.BaseRequest
import com.sorrowblue.comicviewer.domain.usecase.FlowUseCase2
import com.sorrowblue.comicviewer.domain.usecase.GetLibraryInfoError
import com.sorrowblue.comicviewer.domain.usecase.GetNextComicRel

abstract class GetNextBookUseCase :
    FlowUseCase2<GetNextBookUseCase.Request, Book, GetLibraryInfoError>() {

    class Request(val bookshelfId: BookshelfId, val path: String, val relation: GetNextComicRel) :
        BaseRequest {
    }
}
