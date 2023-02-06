package com.sorrowblue.comicviewer.domain.usecase.bookshelf

import com.sorrowblue.comicviewer.domain.entity.BookshelfFolder
import com.sorrowblue.comicviewer.domain.entity.server.BookshelfId
import com.sorrowblue.comicviewer.domain.request.BaseRequest
import com.sorrowblue.comicviewer.domain.usecase.FlowUseCase2
import com.sorrowblue.comicviewer.domain.usecase.GetLibraryInfoError

abstract class GetBookshelfInfoUseCase :
    FlowUseCase2<GetBookshelfInfoUseCase.Request, BookshelfFolder, GetLibraryInfoError>() {

    class Request(val bookshelfId: BookshelfId) : BaseRequest {
        override fun validate(): Boolean {
            return true
        }
    }
}
