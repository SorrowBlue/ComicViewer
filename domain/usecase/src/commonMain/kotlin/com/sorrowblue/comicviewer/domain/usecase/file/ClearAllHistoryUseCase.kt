package com.sorrowblue.comicviewer.domain.usecase.file

import com.sorrowblue.comicviewer.domain.usecase.OneShotUseCase

abstract class ClearAllHistoryUseCase :
    OneShotUseCase<ClearAllHistoryUseCase.Request, Unit, Unit>() {
    data object Request : OneShotUseCase.Request
}
