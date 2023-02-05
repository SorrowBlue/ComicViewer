package com.sorrowblue.comicviewer.domain.usecase

import com.sorrowblue.comicviewer.domain.entity.ServerFolder
import com.sorrowblue.comicviewer.domain.entity.server.ServerId
import com.sorrowblue.comicviewer.domain.request.BaseRequest

abstract class GetServerFolderUseCase :
    FlowUseCase2<GetServerFolderUseCase.Request, ServerFolder, GetLibraryInfoError>() {

    class Request(val serverId: ServerId, val path: String) : BaseRequest {
        override fun validate(): Boolean {
            return true
        }
    }
}

