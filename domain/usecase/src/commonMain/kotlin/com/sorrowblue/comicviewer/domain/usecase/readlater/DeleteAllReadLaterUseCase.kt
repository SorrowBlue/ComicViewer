/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.usecase.readlater

import com.sorrowblue.comicviewer.domain.usecase.OneShotUseCase

abstract class DeleteAllReadLaterUseCase :
    OneShotUseCase<DeleteAllReadLaterUseCase.Request, Unit, Unit>() {
    data object Request : OneShotUseCase.Request
}
