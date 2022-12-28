package com.sorrowblue.comicviewer.domain.usecase

import com.sorrowblue.comicviewer.domain.entity.ServerId
import com.sorrowblue.comicviewer.domain.model.BaseRequest
import java.time.LocalDateTime

enum class GetLibraryFileResult {
    NO_LIBRARY,
    NO_FILE
}

class UpdateLastReadPageRequest(
    val libraryId: ServerId,
    val path: String,
    val lastReadPage: Int,
    val timestamp: LocalDateTime = LocalDateTime.now()
) : BaseRequest {
    override fun validate(): Boolean {
        return true
    }
}

abstract class UpdateLastReadPageUseCase : OneTimeUseCase2<UpdateLastReadPageRequest, Unit, Unit>()
