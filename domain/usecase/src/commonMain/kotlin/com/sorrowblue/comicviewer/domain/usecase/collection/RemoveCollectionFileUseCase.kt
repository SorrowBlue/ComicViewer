package com.sorrowblue.comicviewer.domain.usecase.collection

import com.sorrowblue.comicviewer.domain.model.Resource
import com.sorrowblue.comicviewer.domain.model.collection.CollectionFile
import com.sorrowblue.comicviewer.domain.usecase.UseCase

abstract class RemoveCollectionFileUseCase :
    UseCase<RemoveCollectionFileUseCase.Request, Unit, RemoveCollectionFileUseCase.Error>() {

    class Request(val file: CollectionFile) : UseCase.Request

    sealed interface Error : Resource.AppError {
        data object System : Error
    }
}
