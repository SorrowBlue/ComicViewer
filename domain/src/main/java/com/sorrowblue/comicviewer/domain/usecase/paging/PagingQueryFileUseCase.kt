package com.sorrowblue.comicviewer.domain.usecase.paging

import androidx.paging.PagingConfig
import com.sorrowblue.comicviewer.domain.entity.file.File
import com.sorrowblue.comicviewer.domain.entity.server.Server
import com.sorrowblue.comicviewer.domain.request.BaseRequest

abstract class PagingQueryFileUseCase : PagingUseCase<PagingQueryFileUseCase.Request, File>() {

    class Request(val pagingConfig: PagingConfig, val server: Server, val query: () -> String) :
        BaseRequest {

        override fun validate(): Boolean {
            return true
        }
    }
}
