/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.usecase.bookshelf

import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.common.Resource
import com.sorrowblue.comicviewer.domain.repository.BookshelfRepository
import com.sorrowblue.comicviewer.domain.usecase.OneShotUseCase
import dev.zacsweers.metro.Inject

@Inject
class UpdateDeletionFlagUseCase(private val bookshelfRepository: BookshelfRepository) :
    OneShotUseCase<UpdateDeletionFlagUseCase.Request, Unit, Unit>() {

    data class Request(val bookshelfId: BookshelfId, val deleted: Boolean) : OneShotUseCase.Request

    override suspend fun run(request: Request): Resource<Unit, Unit> {
        bookshelfRepository.updateDeleted(request.bookshelfId, request.deleted)
        return Resource.Success(Unit)
    }
}
