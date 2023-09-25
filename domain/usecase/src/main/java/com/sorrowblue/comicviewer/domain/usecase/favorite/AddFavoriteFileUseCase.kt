package com.sorrowblue.comicviewer.domain.usecase.favorite

import com.sorrowblue.comicviewer.domain.BaseRequest
import com.sorrowblue.comicviewer.domain.model.favorite.FavoriteFile
import com.sorrowblue.comicviewer.domain.usecase.FlowOneUseCase

abstract class AddFavoriteFileUseCase :
    FlowOneUseCase<AddFavoriteFileUseCase.Request, Unit, Unit>() {

    class Request(val favoriteFile: FavoriteFile) : BaseRequest
}
