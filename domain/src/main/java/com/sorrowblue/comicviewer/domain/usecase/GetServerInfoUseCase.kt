package com.sorrowblue.comicviewer.domain.usecase

import com.sorrowblue.comicviewer.domain.entity.ServerFolder
import com.sorrowblue.comicviewer.domain.entity.server.ServerId
import com.sorrowblue.comicviewer.domain.request.BaseRequest

abstract class GetServerInfoUseCase :
    FlowUseCase2<GetServerInfoUseCase.Request, ServerFolder, GetLibraryInfoError>() {

    class Request(val serverId: ServerId) : BaseRequest {
        override fun validate(): Boolean {
            return true
        }
    }
}
