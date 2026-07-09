/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.usecase.file

import com.sorrowblue.comicviewer.domain.model.Resource
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.file.Book
import com.sorrowblue.comicviewer.domain.usecase.UseCase

abstract class GetBookUseCase : UseCase<GetBookUseCase.Request, Book, GetBookUseCase.Error>() {
    data class Request(val bookshelfId: BookshelfId, val path: String) : UseCase.Request

    sealed interface Error : Resource.IError {
        data object NotFound : Error

        data object ReportedSystemError : Error
    }
}
