package com.sorrowblue.comicviewer.domain.usecase.favorite

import com.sorrowblue.comicviewer.domain.model.favorite.Favorite
import com.sorrowblue.comicviewer.domain.usecase.UseCase

abstract class UpdateFavoriteUseCase : UseCase<UpdateFavoriteUseCase.Request, Favorite, Unit>() {
    class Request(val favorite: Favorite) : UseCase.Request
}
