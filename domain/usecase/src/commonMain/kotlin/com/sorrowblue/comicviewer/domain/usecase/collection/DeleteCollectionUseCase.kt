package com.sorrowblue.comicviewer.domain.usecase.collection

import com.sorrowblue.comicviewer.domain.model.collection.CollectionId
import com.sorrowblue.comicviewer.domain.usecase.UseCase

abstract class DeleteCollectionUseCase : UseCase<DeleteCollectionUseCase.Request, Unit, Unit>() {
    class Request(val id: CollectionId) : UseCase.Request
}
