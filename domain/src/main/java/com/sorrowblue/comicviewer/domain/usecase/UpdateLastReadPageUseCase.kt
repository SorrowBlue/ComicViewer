package com.sorrowblue.comicviewer.domain.usecase

import com.sorrowblue.comicviewer.domain.entity.server.ServerId
import com.sorrowblue.comicviewer.domain.request.BaseRequest
import java.time.LocalDateTime

abstract class UpdateLastReadPageUseCase :
    FlowUseCase<UpdateLastReadPageUseCase.Request, Unit, Unit>() {

    class Request(
        val libraryId: ServerId,
        val path: String,
        val lastReadPage: Int,
        val timestamp: LocalDateTime = LocalDateTime.now()
    ) : BaseRequest {
        override fun validate(): Boolean {
            return true
        }
    }
}
