package com.sorrowblue.comicviewer.domain.usecase.favorite

import com.sorrowblue.comicviewer.domain.model.Resource
import com.sorrowblue.comicviewer.domain.model.favorite.Favorite
import com.sorrowblue.comicviewer.domain.usecase.UseCase

abstract class CreateFavoriteUseCase :
    UseCase<CreateFavoriteUseCase.Request, Favorite, CreateFavoriteUseCase.Error>() {

    class Request(val title: String) : UseCase.Request

    sealed interface Error : Resource.AppError {
        data object System : Error
        data object NotFound : Error
    }
}
