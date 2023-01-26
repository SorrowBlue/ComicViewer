package com.sorrowblue.comicviewer.domain.usecase

import com.sorrowblue.comicviewer.domain.entity.server.Server
import com.sorrowblue.comicviewer.domain.request.BaseRequest

abstract class RemoveLibraryUseCase : FlowUseCase<RemoveLibraryUseCase.Request, Boolean, Unit>() {

    class Request(val server: Server) : BaseRequest {
        override fun validate(): Boolean {
            return true
        }
    }
}
