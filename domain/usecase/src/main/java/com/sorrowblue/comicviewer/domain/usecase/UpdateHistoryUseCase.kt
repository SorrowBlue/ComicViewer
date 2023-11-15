package com.sorrowblue.comicviewer.domain.usecase

import com.sorrowblue.comicviewer.domain.BaseRequest
import com.sorrowblue.comicviewer.domain.model.settings.History

abstract class UpdateHistoryUseCase : FlowUseCase<UpdateHistoryUseCase.Request, Unit, Unit>() {

    class Request(val history: History) : BaseRequest
}
