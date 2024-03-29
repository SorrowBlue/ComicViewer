package com.sorrowblue.comicviewer.domain.usecase.favorite

import com.sorrowblue.comicviewer.domain.BaseRequest
import com.sorrowblue.comicviewer.domain.usecase.FlowOneUseCase

abstract class CreateFavoriteUseCase : FlowOneUseCase<CreateFavoriteUseCase.Request, Unit, Unit>() {
    class Request(val title: String) : BaseRequest
}
