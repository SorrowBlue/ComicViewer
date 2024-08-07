package com.sorrowblue.comicviewer.domain.usecase.paging

import androidx.paging.PagingConfig
import com.sorrowblue.comicviewer.domain.BaseRequest
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.favorite.Favorite

abstract class PagingFavoriteUseCase : PagingUseCase<PagingFavoriteUseCase.Request, Favorite>() {

    class Request(
        val pagingConfig: PagingConfig,
        val bookshelfId: BookshelfId,
        val path: String,
        val isRecent: Boolean = false,
    ) : BaseRequest
}
