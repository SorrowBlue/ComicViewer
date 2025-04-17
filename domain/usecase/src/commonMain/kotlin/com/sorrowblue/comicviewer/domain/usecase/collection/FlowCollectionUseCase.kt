package com.sorrowblue.comicviewer.domain.usecase.collection

import com.sorrowblue.comicviewer.domain.model.Resource
import com.sorrowblue.comicviewer.domain.model.collection.Collection
import com.sorrowblue.comicviewer.domain.model.collection.CollectionId
import com.sorrowblue.comicviewer.domain.usecase.UseCase

abstract class FlowCollectionUseCase :
    UseCase<FlowCollectionUseCase.Request, Collection, FlowCollectionUseCase.Error>() {

    class Request(val id: CollectionId) : UseCase.Request

    sealed interface Error : Resource.AppError {
        data object System : Error
        data object NotFound : Error
    }
}
