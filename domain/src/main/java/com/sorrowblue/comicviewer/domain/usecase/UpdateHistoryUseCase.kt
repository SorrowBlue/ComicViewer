package com.sorrowblue.comicviewer.domain.usecase

import com.sorrowblue.comicviewer.domain.model.settings.History
import com.sorrowblue.comicviewer.domain.request.BaseRequest

abstract class UpdateHistoryUseCase : FlowUseCase<UpdateHistoryUseCase.Request, Unit, Unit>() {

    class Request(val history: History) : BaseRequest {
    }

}
