package com.sorrowblue.comicviewer.domain.usecase

import com.sorrowblue.comicviewer.domain.entity.ReadLater
import com.sorrowblue.comicviewer.domain.entity.server.ServerId
import com.sorrowblue.comicviewer.domain.request.BaseRequest

abstract class AddReadLaterUseCase :
    FlowOneUseCase<AddReadLaterUseCase.Request, ReadLater, Unit>() {
    class Request(val serverId: ServerId, val path: String) : BaseRequest {
        override fun validate(): Boolean {
            return true
        }
    }
}
