/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.usecase.file

import com.sorrowblue.comicviewer.domain.model.file.Book
import com.sorrowblue.comicviewer.domain.usecase.OneShotUseCase

abstract class CloseBookUseCase : OneShotUseCase<CloseBookUseCase.Request, Unit, Unit>() {
    data class Request(val book: Book) : OneShotUseCase.Request
}
