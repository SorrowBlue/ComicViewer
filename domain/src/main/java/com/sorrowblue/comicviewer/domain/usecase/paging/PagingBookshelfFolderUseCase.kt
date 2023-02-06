package com.sorrowblue.comicviewer.domain.usecase.paging

import androidx.paging.PagingConfig
import com.sorrowblue.comicviewer.domain.entity.BookshelfFolder
import com.sorrowblue.comicviewer.domain.request.BaseRequest

abstract class PagingBookshelfFolderUseCase : PagingUseCase<PagingBookshelfFolderUseCase.Request, BookshelfFolder>() {

    class Request(val pagingConfig: PagingConfig) : BaseRequest {
        override fun validate(): Boolean {
            return true
        }
    }
}
