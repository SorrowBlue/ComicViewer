package com.sorrowblue.comicviewer.domain.usecase.collection

import androidx.paging.PagingConfig
import com.sorrowblue.comicviewer.domain.BaseRequest
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.collection.Collection
import com.sorrowblue.comicviewer.domain.usecase.PagingUseCase

abstract class PagingCollectionExistUseCase :
    PagingUseCase<PagingCollectionExistUseCase.Request, Pair<Collection, Boolean>>() {

    data class Request(
        val pagingConfig: PagingConfig,
        val bookshelfId: BookshelfId,
        val path: String,
        val isRecent: Boolean,
    ) : BaseRequest
}
