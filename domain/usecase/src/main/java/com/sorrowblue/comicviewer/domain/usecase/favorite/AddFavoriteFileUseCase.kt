package com.sorrowblue.comicviewer.domain.usecase.favorite

import com.sorrowblue.comicviewer.domain.model.Resource
import com.sorrowblue.comicviewer.domain.model.favorite.FavoriteFile
import com.sorrowblue.comicviewer.domain.usecase.UseCase

abstract class AddFavoriteFileUseCase :
    UseCase<AddFavoriteFileUseCase.Request, Unit, AddFavoriteFileUseCase.Error>() {

    class Request(val favoriteFile: FavoriteFile) : UseCase.Request

    sealed interface Error : Resource.AppError {
        data object System : Error
        data object NotFound : Error
    }
}
