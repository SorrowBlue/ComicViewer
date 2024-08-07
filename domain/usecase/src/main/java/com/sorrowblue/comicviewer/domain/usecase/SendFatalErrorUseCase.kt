package com.sorrowblue.comicviewer.domain.usecase

import com.sorrowblue.comicviewer.domain.model.Resource

abstract class SendFatalErrorUseCase :
    UseCase<SendFatalErrorUseCase.Request, Unit, SendFatalErrorUseCase.Error>() {

    data class Request(val throwable: Throwable) : UseCase.Request
    sealed interface Error : Resource.AppError {
        data object System : Error
    }
}
