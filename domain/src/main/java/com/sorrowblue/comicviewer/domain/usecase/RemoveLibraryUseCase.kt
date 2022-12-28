package com.sorrowblue.comicviewer.domain.usecase

import com.sorrowblue.comicviewer.domain.entity.Server
import com.sorrowblue.comicviewer.domain.model.BaseRequest

class RemoveLibraryRequest(val server: Server) : BaseRequest {
    override fun validate(): Boolean {
        return true
    }
}

abstract class RemoveLibraryUseCase : MultipleUseCase<RemoveLibraryRequest, Boolean>()
