package com.sorrowblue.comicviewer.domain.usecase.favorite

import com.sorrowblue.comicviewer.domain.entity.favorite.FavoriteFile
import com.sorrowblue.comicviewer.domain.request.BaseRequest
import com.sorrowblue.comicviewer.domain.usecase.FlowOneUseCase

abstract class AddFavoriteFileUseCase :
    FlowOneUseCase<AddFavoriteFileUseCase.Request, Unit, Unit>() {

    class Request(val favoriteFile: FavoriteFile) : BaseRequest {
        override fun validate(): Boolean {
            return true
        }
    }
}
