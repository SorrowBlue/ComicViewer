package com.sorrowblue.comicviewer.domain.usecase.bookshelf

import com.sorrowblue.comicviewer.domain.BaseRequest
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.usecase.FlowUseCase2
import com.sorrowblue.comicviewer.domain.usecase.GetLibraryInfoError

abstract class DeleteHistoryUseCase :
    FlowUseCase2<DeleteHistoryUseCase.Request, Unit, GetLibraryInfoError>() {

    class Request(val bookshelfId: BookshelfId, val list: List<String>) : BaseRequest
}
