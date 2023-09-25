package com.sorrowblue.comicviewer.domain.usecase.paging

import androidx.paging.PagingConfig
import com.sorrowblue.comicviewer.domain.BaseRequest
import com.sorrowblue.comicviewer.domain.model.file.File

abstract class PagingReadLaterFileUseCase :
    PagingUseCase<PagingReadLaterFileUseCase.Request, File>() {

    class Request(val pagingConfig: PagingConfig) : BaseRequest
}
