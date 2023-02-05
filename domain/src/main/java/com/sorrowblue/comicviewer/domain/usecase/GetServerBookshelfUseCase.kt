package com.sorrowblue.comicviewer.domain.usecase

import com.sorrowblue.comicviewer.domain.entity.ServerBookshelf
import com.sorrowblue.comicviewer.domain.entity.server.ServerId
import com.sorrowblue.comicviewer.domain.request.BaseRequest

abstract class GetServerBookshelfUseCase :
    FlowUseCase2<GetServerBookshelfUseCase.Request, ServerBookshelf, GetLibraryInfoError>() {

    class Request(val serverId: ServerId, val path: String) : BaseRequest {
        override fun validate(): Boolean {
            return true
        }
    }
}

