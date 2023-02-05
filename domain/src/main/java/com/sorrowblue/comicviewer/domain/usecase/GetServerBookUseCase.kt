package com.sorrowblue.comicviewer.domain.usecase

import com.sorrowblue.comicviewer.domain.entity.ServerBook
import com.sorrowblue.comicviewer.domain.entity.file.Book
import com.sorrowblue.comicviewer.domain.entity.server.ServerId
import com.sorrowblue.comicviewer.domain.request.BaseRequest

abstract class GetServerBookUseCase :
    FlowUseCase2<GetServerBookUseCase.Request, ServerBook, GetLibraryFileResult>() {

    class Request(val serverId: ServerId, val path: String) : BaseRequest {
        override fun validate(): Boolean {
            return true
        }
    }
}

abstract class GetBookUseCase : FlowUseCase2<GetBookUseCase.Request, Book, Unit>() {

    class Request(val serverId: ServerId, val path: String) : BaseRequest {
        override fun validate(): Boolean {
            return true
        }
    }
}
