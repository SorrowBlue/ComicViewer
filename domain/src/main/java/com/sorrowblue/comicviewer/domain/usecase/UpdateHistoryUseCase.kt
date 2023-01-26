package com.sorrowblue.comicviewer.domain.usecase

import com.sorrowblue.comicviewer.domain.entity.settings.History
import com.sorrowblue.comicviewer.domain.request.BaseRequest

abstract class UpdateHistoryUseCase : FlowUseCase<UpdateHistoryUseCase.Request, Unit, Unit>() {

    class Request(val history: History) : BaseRequest {
        override fun validate(): Boolean {
            return true
        }
    }

}
