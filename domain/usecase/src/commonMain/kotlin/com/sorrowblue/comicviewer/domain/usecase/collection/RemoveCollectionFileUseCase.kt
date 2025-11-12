package com.sorrowblue.comicviewer.domain.usecase.collection

import com.sorrowblue.comicviewer.domain.model.Resource
import com.sorrowblue.comicviewer.domain.model.collection.CollectionFile
import com.sorrowblue.comicviewer.domain.usecase.OneShotUseCase

abstract class RemoveCollectionFileUseCase :
    OneShotUseCase<RemoveCollectionFileUseCase.Request, Unit, RemoveCollectionFileUseCase.Error>() {
    class Request(val file: CollectionFile) : OneShotUseCase.Request

    sealed interface Error : Resource.AppError {
        data object System : Error
    }
}
