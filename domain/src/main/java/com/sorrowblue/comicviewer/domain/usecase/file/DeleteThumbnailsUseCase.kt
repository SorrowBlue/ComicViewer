package com.sorrowblue.comicviewer.domain.usecase.file

import com.sorrowblue.comicviewer.domain.request.BaseRequest
import com.sorrowblue.comicviewer.domain.usecase.FlowOneUseCase
import com.sorrowblue.comicviewer.domain.usecase.GetLibraryInfoError

abstract class DeleteThumbnailsUseCase :
    FlowOneUseCase<DeleteThumbnailsUseCase.Request, Unit, GetLibraryInfoError>() {

    object Request : BaseRequest {
    }
}
