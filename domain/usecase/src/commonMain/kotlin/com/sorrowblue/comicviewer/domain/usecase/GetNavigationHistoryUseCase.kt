/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.usecase

import com.sorrowblue.comicviewer.domain.EmptyRequest
import com.sorrowblue.comicviewer.domain.model.Resource

abstract class GetNavigationHistoryUseCase :
    UseCase<EmptyRequest, NavigationHistory, GetNavigationHistoryUseCase.Error>() {
    sealed interface Error : Resource.AppError {
        data object System : Error
    }
}
