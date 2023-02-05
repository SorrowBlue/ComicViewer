package com.sorrowblue.comicviewer.domain.usecase

import com.sorrowblue.comicviewer.domain.entity.file.Book
import com.sorrowblue.comicviewer.domain.entity.server.ServerId
import com.sorrowblue.comicviewer.domain.request.BaseRequest

abstract class GetNextBookUseCase :
    FlowUseCase2<GetNextBookUseCase.Request, Book, GetLibraryInfoError>() {

    class Request(val serverId: ServerId, val path: String, val relation: GetNextComicRel) :
        BaseRequest {
        override fun validate(): Boolean {
            return true
        }
    }
}
