package com.sorrowblue.comicviewer.domain.usecase

import com.sorrowblue.comicviewer.domain.entity.RegisterServerRequest
import com.sorrowblue.comicviewer.domain.entity.Server

abstract class RegisterLibraryUseCase :
    OneTimeUseCase2<RegisterServerRequest, Server, RegisterLibraryError>()

enum class RegisterLibraryError {
    NO_EXISTS,
    LOGON_FAILURE,
    BAD_NETWORK_NAME,
    UNKNOWN
}
