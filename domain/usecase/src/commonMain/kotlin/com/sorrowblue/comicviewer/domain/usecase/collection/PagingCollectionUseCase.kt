package com.sorrowblue.comicviewer.domain.usecase.collection

import androidx.paging.PagingConfig
import com.sorrowblue.comicviewer.domain.BaseRequest
import com.sorrowblue.comicviewer.domain.model.collection.Collection
import com.sorrowblue.comicviewer.domain.usecase.PagingUseCase

abstract class PagingCollectionUseCase :
    PagingUseCase<PagingCollectionUseCase.Request, Collection>() {
    data class Request(val pagingConfig: PagingConfig) : BaseRequest
}
