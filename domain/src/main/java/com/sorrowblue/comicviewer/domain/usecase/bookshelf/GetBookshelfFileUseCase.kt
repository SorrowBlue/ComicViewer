package com.sorrowblue.comicviewer.domain.usecase.bookshelf

import com.sorrowblue.comicviewer.domain.entity.BookshelfFile
import com.sorrowblue.comicviewer.domain.entity.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.request.BaseRequest
import com.sorrowblue.comicviewer.domain.usecase.FlowUseCase2
import com.sorrowblue.comicviewer.domain.usecase.GetLibraryFileResult

abstract class GetBookshelfFileUseCase :
    FlowUseCase2<GetBookshelfFileUseCase.Request, BookshelfFile, GetLibraryFileResult>() {

    class Request(val bookshelfId: BookshelfId, val path: String) : BaseRequest {
        override fun validate(): Boolean {
            return true
        }
    }
}
