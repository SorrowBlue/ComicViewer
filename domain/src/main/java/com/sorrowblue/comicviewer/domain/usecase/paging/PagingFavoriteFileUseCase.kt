package com.sorrowblue.comicviewer.domain.usecase.paging

import androidx.paging.PagingConfig
import com.sorrowblue.comicviewer.domain.entity.favorite.FavoriteId
import com.sorrowblue.comicviewer.domain.entity.file.File
import com.sorrowblue.comicviewer.domain.request.BaseRequest

abstract class PagingFavoriteFileUseCase :
    PagingUseCase<PagingFavoriteFileUseCase.Request, File>() {

    class Request(val pagingConfig: PagingConfig, val favoriteId: FavoriteId) : BaseRequest {
        override fun validate(): Boolean {
            return true
        }
    }
}
