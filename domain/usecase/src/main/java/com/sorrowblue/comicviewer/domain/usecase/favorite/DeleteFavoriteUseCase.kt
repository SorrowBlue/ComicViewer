package com.sorrowblue.comicviewer.domain.usecase.favorite

import com.sorrowblue.comicviewer.domain.model.favorite.FavoriteId
import com.sorrowblue.comicviewer.domain.usecase.UseCase

abstract class DeleteFavoriteUseCase : UseCase<DeleteFavoriteUseCase.Request, Unit, Unit>() {
    class Request(val favoriteId: FavoriteId) : UseCase.Request
}
