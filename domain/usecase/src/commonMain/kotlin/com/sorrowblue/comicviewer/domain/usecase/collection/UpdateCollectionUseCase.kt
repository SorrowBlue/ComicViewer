package com.sorrowblue.comicviewer.domain.usecase.collection

import com.sorrowblue.comicviewer.domain.model.Resource
import com.sorrowblue.comicviewer.domain.model.collection.Collection
import com.sorrowblue.comicviewer.domain.usecase.UseCase

abstract class UpdateCollectionUseCase :
    UseCase<UpdateCollectionUseCase.Request, Unit, UpdateCollectionUseCase.Error>() {

    class Request(val collection: Collection) : UseCase.Request

    sealed interface Error : Resource.AppError {
        data object System : Error
    }
}
