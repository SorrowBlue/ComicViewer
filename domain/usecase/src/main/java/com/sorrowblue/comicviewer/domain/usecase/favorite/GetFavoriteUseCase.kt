package com.sorrowblue.comicviewer.domain.usecase.favorite

import com.sorrowblue.comicviewer.domain.BaseRequest
import com.sorrowblue.comicviewer.domain.model.favorite.Favorite
import com.sorrowblue.comicviewer.domain.model.favorite.FavoriteId
import com.sorrowblue.comicviewer.domain.usecase.FlowUseCase2

abstract class GetFavoriteUseCase : FlowUseCase2<GetFavoriteUseCase.Request, Favorite, Unit>() {
    class Request(val favoriteId: FavoriteId) : BaseRequest
}
