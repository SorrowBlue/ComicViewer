package com.sorrowblue.comicviewer.domain.usecase.paging

import androidx.paging.PagingConfig
import com.sorrowblue.comicviewer.domain.entity.ServerFolder
import com.sorrowblue.comicviewer.domain.request.BaseRequest

abstract class PagingServerUseCase : PagingUseCase<PagingServerUseCase.Request, ServerFolder>() {

    class Request(val pagingConfig: PagingConfig) : BaseRequest {
        override fun validate(): Boolean {
            return true
        }
    }
}
