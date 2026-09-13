/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.usecase.file

import com.sorrowblue.comicviewer.domain.model.common.Resource
import com.sorrowblue.comicviewer.domain.model.file.Book
import com.sorrowblue.comicviewer.domain.repository.file.BookFileReaderManager
import com.sorrowblue.comicviewer.domain.usecase.OneShotUseCase
import dev.zacsweers.metro.Inject

@Inject
class CloseBookUseCase(private val bookFileReaderManager: BookFileReaderManager) :
    OneShotUseCase<CloseBookUseCase.Request, Unit, Unit>() {

    data class Request(val book: Book) : OneShotUseCase.Request

    override suspend fun run(request: Request): Resource<Unit, Unit> {
        bookFileReaderManager.close(request.book)
        return Resource.Success(Unit)
    }
}
