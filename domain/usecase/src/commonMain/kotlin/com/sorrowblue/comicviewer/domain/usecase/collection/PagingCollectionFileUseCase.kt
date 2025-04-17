package com.sorrowblue.comicviewer.domain.usecase.collection

import androidx.paging.PagingConfig
import com.sorrowblue.comicviewer.domain.BaseRequest
import com.sorrowblue.comicviewer.domain.model.collection.CollectionId
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.usecase.PagingUseCase

abstract class PagingCollectionFileUseCase :
    PagingUseCase<PagingCollectionFileUseCase.Request, File>() {

    class Request(val pagingConfig: PagingConfig, val collectionId: CollectionId) : BaseRequest
}
