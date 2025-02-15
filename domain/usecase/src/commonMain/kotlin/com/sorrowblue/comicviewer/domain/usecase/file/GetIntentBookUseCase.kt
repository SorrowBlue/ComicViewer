package com.sorrowblue.comicviewer.domain.usecase.file

import com.sorrowblue.comicviewer.domain.model.Resource
import com.sorrowblue.comicviewer.domain.model.file.BookFile
import com.sorrowblue.comicviewer.domain.usecase.UseCase

abstract class GetIntentBookUseCase :
    UseCase<GetIntentBookUseCase.Request, BookFile, GetIntentBookUseCase.Error>() {

    class Request(val data: String) : UseCase.Request

    enum class Error : Resource.AppError {
        System,
    }
}
