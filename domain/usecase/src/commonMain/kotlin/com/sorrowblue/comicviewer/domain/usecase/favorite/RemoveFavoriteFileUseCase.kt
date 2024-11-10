package com.sorrowblue.comicviewer.domain.usecase.favorite

import com.sorrowblue.comicviewer.domain.model.Resource
import com.sorrowblue.comicviewer.domain.model.favorite.FavoriteFile
import com.sorrowblue.comicviewer.domain.usecase.UseCase

abstract class RemoveFavoriteFileUseCase :
    UseCase<RemoveFavoriteFileUseCase.Request, Unit, Resource.SystemError>() {

    class Request(val favoriteFile: FavoriteFile) : UseCase.Request
}
