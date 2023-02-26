package com.sorrowblue.comicviewer.domain.usecase.file

import com.sorrowblue.comicviewer.domain.entity.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.entity.file.Book
import com.sorrowblue.comicviewer.domain.request.BaseRequest
import com.sorrowblue.comicviewer.domain.usecase.FlowUseCase2
import com.sorrowblue.comicviewer.domain.usecase.GetLibraryInfoError
import com.sorrowblue.comicviewer.domain.usecase.GetNextComicRel

abstract class GetNextBookUseCase :
    FlowUseCase2<GetNextBookUseCase.Request, Book, GetLibraryInfoError>() {

    class Request(val bookshelfId: BookshelfId, val path: String, val relation: GetNextComicRel) :
        BaseRequest {
        override fun validate(): Boolean {
            return true
        }
    }
}
