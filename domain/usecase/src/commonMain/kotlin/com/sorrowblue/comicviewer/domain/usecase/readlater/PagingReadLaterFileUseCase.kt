package com.sorrowblue.comicviewer.domain.usecase.readlater

import androidx.paging.PagingConfig
import com.sorrowblue.comicviewer.domain.BaseRequest
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.usecase.PagingUseCase

abstract class PagingReadLaterFileUseCase :
    PagingUseCase<PagingReadLaterFileUseCase.Request, File>() {
    class Request(val pagingConfig: PagingConfig) : BaseRequest
}
