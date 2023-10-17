package com.sorrowblue.comicviewer.domain.usecase.paging

import androidx.paging.PagingConfig
import com.sorrowblue.comicviewer.domain.BaseRequest
import com.sorrowblue.comicviewer.domain.model.BookshelfFolder

abstract class PagingBookshelfFolderUseCase :
    PagingUseCase<PagingBookshelfFolderUseCase.Request, BookshelfFolder>() {

    class Request(val pagingConfig: PagingConfig) : BaseRequest
}
