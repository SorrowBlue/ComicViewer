package com.sorrowblue.comicviewer.domain.usecase.favorite

import com.sorrowblue.comicviewer.domain.model.favorite.Favorite
import com.sorrowblue.comicviewer.domain.request.BaseRequest
import com.sorrowblue.comicviewer.domain.usecase.FlowOneUseCase

abstract class UpdateFavoriteUseCase :
    FlowOneUseCase<UpdateFavoriteUseCase.Request, Favorite, Unit>() {
    class Request(val favorite: Favorite) : BaseRequest {
    }
}
