package com.sorrowblue.comicviewer.domain.usecase

import com.sorrowblue.comicviewer.domain.entity.ReadLaterFile
import com.sorrowblue.comicviewer.domain.entity.server.BookshelfId
import com.sorrowblue.comicviewer.domain.request.BaseRequest

abstract class AddReadLaterUseCase :
    FlowOneUseCase<AddReadLaterUseCase.Request, ReadLaterFile, Unit>() {
    class Request(val bookshelfId: BookshelfId, val path: String) : BaseRequest {
        override fun validate(): Boolean {
            return true
        }
    }
}
