package com.sorrowblue.comicviewer.domain.usecase.paging

import androidx.paging.PagingConfig
import com.sorrowblue.comicviewer.domain.BaseRequest
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.file.BookThumbnail
import com.sorrowblue.comicviewer.domain.usecase.PagingUseCase

abstract class PagingBookshelfBookUseCase :
    PagingUseCase<PagingBookshelfBookUseCase.Request, BookThumbnail>() {

    class Request(val bookshelfId: BookshelfId, val pagingConfig: PagingConfig) : BaseRequest
}
