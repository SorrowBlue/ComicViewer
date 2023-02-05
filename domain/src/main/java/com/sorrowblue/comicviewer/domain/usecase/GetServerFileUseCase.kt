package com.sorrowblue.comicviewer.domain.usecase

import com.sorrowblue.comicviewer.domain.entity.ServerFile
import com.sorrowblue.comicviewer.domain.entity.server.ServerId
import com.sorrowblue.comicviewer.domain.request.BaseRequest

abstract class GetServerFileUseCase :
    FlowUseCase2<GetServerFileUseCase.Request, ServerFile, GetLibraryFileResult>() {

    class Request(val serverId: ServerId, val path: String) : BaseRequest {
        override fun validate(): Boolean {
            return true
        }
    }
}
