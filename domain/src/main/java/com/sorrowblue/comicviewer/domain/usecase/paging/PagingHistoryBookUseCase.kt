package com.sorrowblue.comicviewer.domain.usecase.paging

import androidx.paging.PagingConfig
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.request.BaseRequest

abstract class PagingHistoryBookUseCase : PagingUseCase<PagingHistoryBookUseCase.Request, File>() {

    class Request(val pagingConfig: PagingConfig) : BaseRequest {
    }
}
