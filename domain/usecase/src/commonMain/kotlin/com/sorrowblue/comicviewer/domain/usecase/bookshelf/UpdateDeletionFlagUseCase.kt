/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.usecase.bookshelf

import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.common.Resource
import com.sorrowblue.comicviewer.domain.repository.BookshelfRepository
import com.sorrowblue.comicviewer.domain.usecase.OneShotUseCase
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject

abstract class UpdateDeletionFlagUseCase :
    OneShotUseCase<UpdateDeletionFlagUseCase.Request, Unit, Unit>() {

    data class Request(val bookshelfId: BookshelfId, val deleted: Boolean)
}

@Inject
@ContributesBinding(AppScope::class)
internal class UpdateDeletionFlagUseCaseImpl(
    private val bookshelfRepository: BookshelfRepository,
) : UpdateDeletionFlagUseCase() {

    override suspend fun run(request: Request): Resource<Unit, Unit> {
        bookshelfRepository.updateDeleted(request.bookshelfId, request.deleted)
        return Resource.Success(Unit)
    }
}
