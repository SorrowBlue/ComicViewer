package com.sorrowblue.comicviewer.domain.usecase

import com.sorrowblue.comicviewer.domain.entity.server.Server
import com.sorrowblue.comicviewer.domain.request.BaseRequest

abstract class RegisterServerUseCase :
    FlowOneUseCase<RegisterServerUseCase.Request, Server, RegisterServerError>() {

    class Request(val server: Server, val path: String) : BaseRequest {
        override fun validate(): Boolean {
            return true
        }
    }
}

sealed class RegisterServerError : UseCaseError {
    object InvalidServerInfo : RegisterServerError()
    object InvalidAuth : RegisterServerError()
    object InvalidPath : RegisterServerError()
}
