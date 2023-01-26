package com.sorrowblue.comicviewer.domain.usecase.paging

import androidx.paging.PagingConfig
import com.sorrowblue.comicviewer.domain.entity.Favorite
import com.sorrowblue.comicviewer.domain.entity.FavoriteId
import com.sorrowblue.comicviewer.domain.entity.file.Bookshelf
import com.sorrowblue.comicviewer.domain.entity.file.File
import com.sorrowblue.comicviewer.domain.entity.server.Server
import com.sorrowblue.comicviewer.domain.request.BaseRequest

abstract class PagingFileUseCase : PagingUseCase<PagingFileUseCase.Request, File>() {

    class Request(val pagingConfig: PagingConfig, val server: Server, val bookshelf: Bookshelf) :
        BaseRequest {
        override fun validate(): Boolean {
            return true
        }
    }
}

abstract class PagingFavoriteUseCase : PagingUseCase<PagingFavoriteUseCase.Request, Favorite>() {

    class Request(val pagingConfig: PagingConfig) : BaseRequest {
        override fun validate(): Boolean {
            return true
        }
    }
}

abstract class PagingFavoriteBookUseCase :
    PagingUseCase<PagingFavoriteBookUseCase.Request, File>() {

    class Request(val pagingConfig: PagingConfig, val favoriteId: FavoriteId) : BaseRequest {
        override fun validate(): Boolean {
            return true
        }
    }
}
