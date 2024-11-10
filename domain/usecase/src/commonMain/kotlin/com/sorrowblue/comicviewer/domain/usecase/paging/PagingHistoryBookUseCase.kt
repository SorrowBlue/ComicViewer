package com.sorrowblue.comicviewer.domain.usecase.paging

import androidx.paging.PagingConfig
import com.sorrowblue.comicviewer.domain.BaseRequest
import com.sorrowblue.comicviewer.domain.model.file.Book
import com.sorrowblue.comicviewer.domain.usecase.PagingUseCase

abstract class PagingHistoryBookUseCase : PagingUseCase<PagingHistoryBookUseCase.Request, Book>() {

    class Request(val pagingConfig: PagingConfig) : BaseRequest
}
