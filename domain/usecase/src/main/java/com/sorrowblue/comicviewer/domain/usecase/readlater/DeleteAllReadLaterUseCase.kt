package com.sorrowblue.comicviewer.domain.usecase.readlater

import com.sorrowblue.comicviewer.domain.model.Resource
import com.sorrowblue.comicviewer.domain.usecase.UseCase

abstract class DeleteAllReadLaterUseCase :
    UseCase<DeleteAllReadLaterUseCase.Request, Unit, DeleteAllReadLaterUseCase.Error>() {

    data object Request : UseCase.Request

    enum class Error : Resource.AppError {
        System,
    }
}
