/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.service.interactor.bookshelf

import com.sorrowblue.comicviewer.domain.model.common.Resource
import com.sorrowblue.comicviewer.domain.repository.BookshelfRepository
import com.sorrowblue.comicviewer.domain.usecase.bookshelf.UpdateDeletionFlagUseCase
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding

@ContributesBinding(AppScope::class)
internal class UpdateDeletionFlagInteractor(private val bookshelfRepository: BookshelfRepository) :
    UpdateDeletionFlagUseCase() {
    override suspend fun run(request: Request): Resource<Unit, Unit> {
        bookshelfRepository.updateDeleted(request.bookshelfId, request.deleted)
        return Resource.Success(Unit)
    }
}
