package com.sorrowblue.comicviewer.domain.usecase.paging

import androidx.paging.PagingConfig
import com.sorrowblue.comicviewer.domain.BaseRequest
import com.sorrowblue.comicviewer.domain.model.favorite.FavoriteId
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.usecase.PagingUseCase

abstract class PagingFavoriteFileUseCase :
    PagingUseCase<PagingFavoriteFileUseCase.Request, File>() {

    class Request(val pagingConfig: PagingConfig, val favoriteId: FavoriteId) : BaseRequest
}
