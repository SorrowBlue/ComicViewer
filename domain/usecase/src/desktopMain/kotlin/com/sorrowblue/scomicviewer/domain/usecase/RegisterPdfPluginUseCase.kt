package com.sorrowblue.scomicviewer.domain.usecase

import com.sorrowblue.comicviewer.domain.usecase.UseCase

abstract class RegisterPdfPluginUseCase :
    UseCase<RegisterPdfPluginUseCase.Request, RegisterPdfPluginUseCase.Result, RegisterPdfPluginUseCase.Error>() {
    data class Request(val rootPath: String) : UseCase.Request

    data class Result(val version: String)

    sealed interface Error {
        data object NotFound : Error

        data object NotSupportVersion : Error
    }
}
