package com.sorrowblue.comicviewer.domain.usecase.bookshelf

import androidx.paging.PagingConfig
import com.sorrowblue.comicviewer.domain.BaseRequest
import com.sorrowblue.comicviewer.domain.model.BookshelfFolder
import com.sorrowblue.comicviewer.domain.usecase.PagingUseCase

abstract class PagingBookshelfFolderUseCase :
    PagingUseCase<PagingBookshelfFolderUseCase.Request, BookshelfFolder>() {
    class Request(val pagingConfig: PagingConfig) : BaseRequest
}
