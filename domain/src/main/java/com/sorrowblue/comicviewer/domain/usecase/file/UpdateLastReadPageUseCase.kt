package com.sorrowblue.comicviewer.domain.usecase.file

import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.request.BaseRequest
import com.sorrowblue.comicviewer.domain.usecase.FlowUseCase
import java.time.LocalDateTime

abstract class UpdateLastReadPageUseCase :
    FlowUseCase<UpdateLastReadPageUseCase.Request, Unit, Unit>() {

    class Request(
        val libraryId: BookshelfId,
        val path: String,
        val lastReadPage: Int,
        val timestamp: LocalDateTime = LocalDateTime.now()
    ) : BaseRequest {
    }
}
