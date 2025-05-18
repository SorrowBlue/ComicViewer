package com.sorrowblue.comicviewer.domain.usecase.collection

import com.sorrowblue.comicviewer.domain.model.collection.CollectionId
import com.sorrowblue.comicviewer.domain.usecase.OneShotUseCase

abstract class DeleteCollectionUseCase :
    OneShotUseCase<DeleteCollectionUseCase.Request, Unit, Unit>() {
    class Request(val id: CollectionId) : OneShotUseCase.Request
}
