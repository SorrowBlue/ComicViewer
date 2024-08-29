package com.sorrowblue.comicviewer.domain.usecase.paging

import androidx.paging.PagingConfig
import com.sorrowblue.comicviewer.domain.BaseRequest
import com.sorrowblue.comicviewer.domain.model.Resource
import com.sorrowblue.comicviewer.domain.model.SearchCondition
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.usecase.PagingUseCase

abstract class PagingQueryFileUseCase :
    PagingUseCase<PagingQueryFileUseCase.Request, File>() {

    class Request(
        val pagingConfig: PagingConfig,
        val bookshelfId: BookshelfId,
        val searchCondition: () -> SearchCondition,
    ) : BaseRequest

    enum class Error : Resource.AppError {
        NOT_FOUND,
    }
}
