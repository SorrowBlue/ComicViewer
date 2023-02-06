package com.sorrowblue.comicviewer.domain.usecase.favorite

import com.sorrowblue.comicviewer.domain.entity.favorite.Favorite
import com.sorrowblue.comicviewer.domain.entity.server.BookshelfId
import com.sorrowblue.comicviewer.domain.request.BaseRequest
import com.sorrowblue.comicviewer.domain.usecase.FlowUseCase

abstract class GetFavoriteListUseCase :
    FlowUseCase<GetFavoriteListUseCase.Request, List<Favorite>, Unit>() {
    class Request(val bookshelfId: BookshelfId, val filePath: String) : BaseRequest {
        override fun validate(): Boolean {
            return true
        }
    }
}

