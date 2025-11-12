package com.sorrowblue.comicviewer.domain.usecase.collection

import com.sorrowblue.comicviewer.domain.model.Resource
import com.sorrowblue.comicviewer.domain.model.collection.Collection
import com.sorrowblue.comicviewer.domain.usecase.OneShotUseCase

abstract class UpdateCollectionUseCase :
    OneShotUseCase<UpdateCollectionUseCase.Request, Unit, UpdateCollectionUseCase.Error>() {
    data class Request(val collection: Collection) : OneShotUseCase.Request

    sealed interface Error : Resource.AppError {
        data object System : Error
    }
}
