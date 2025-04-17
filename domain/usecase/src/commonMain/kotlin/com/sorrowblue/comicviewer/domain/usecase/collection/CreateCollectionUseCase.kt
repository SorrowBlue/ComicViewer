package com.sorrowblue.comicviewer.domain.usecase.collection

import com.sorrowblue.comicviewer.domain.model.Resource
import com.sorrowblue.comicviewer.domain.model.collection.Collection
import com.sorrowblue.comicviewer.domain.usecase.UseCase

abstract class CreateCollectionUseCase :
    UseCase<CreateCollectionUseCase.Request, Collection, CreateCollectionUseCase.Error>() {

    class Request(val collection: Collection) : UseCase.Request

    sealed interface Error : Resource.AppError {
        data object System : Error
        data object NotFound : Error
    }
}
