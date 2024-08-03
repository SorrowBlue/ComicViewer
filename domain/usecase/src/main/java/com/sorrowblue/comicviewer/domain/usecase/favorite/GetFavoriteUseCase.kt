package com.sorrowblue.comicviewer.domain.usecase.favorite

import com.sorrowblue.comicviewer.domain.model.Resource
import com.sorrowblue.comicviewer.domain.model.favorite.Favorite
import com.sorrowblue.comicviewer.domain.model.favorite.FavoriteId
import com.sorrowblue.comicviewer.domain.usecase.UseCase

abstract class GetFavoriteUseCase :
    UseCase<GetFavoriteUseCase.Request, Favorite, GetFavoriteUseCase.Error>() {
    class Request(val favoriteId: FavoriteId) : UseCase.Request

    enum class Error : Resource.AppError {
        System,
    }
}
