package com.sorrowblue.comicviewer.domain.usecase.paging

import androidx.paging.PagingConfig
import com.sorrowblue.comicviewer.domain.entity.file.File
import com.sorrowblue.comicviewer.domain.request.BaseRequest

abstract class PagingReadLaterUseCase : PagingUseCase<PagingReadLaterUseCase.Request, File>() {

    class Request(val pagingConfig: PagingConfig) : BaseRequest {
        override fun validate(): Boolean {
            return true
        }
    }
}
