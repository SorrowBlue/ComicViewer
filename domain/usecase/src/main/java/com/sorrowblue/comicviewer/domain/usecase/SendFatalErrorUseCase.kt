package com.sorrowblue.comicviewer.domain.usecase

abstract class SendFatalErrorUseCase : OneShotUseCase<SendFatalErrorUseCase.Request, Unit, Unit>() {

    data class Request(val throwable: Throwable) : OneShotUseCase.Request
}
