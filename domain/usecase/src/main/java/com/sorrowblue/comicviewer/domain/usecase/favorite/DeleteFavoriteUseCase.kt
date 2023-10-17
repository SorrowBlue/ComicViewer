package com.sorrowblue.comicviewer.domain.usecase.favorite

import com.sorrowblue.comicviewer.domain.BaseRequest
import com.sorrowblue.comicviewer.domain.model.favorite.FavoriteId
import com.sorrowblue.comicviewer.domain.usecase.FlowOneUseCase

abstract class DeleteFavoriteUseCase : FlowOneUseCase<DeleteFavoriteUseCase.Request, Unit, Unit>() {
    class Request(val favoriteId: FavoriteId) : BaseRequest
}
