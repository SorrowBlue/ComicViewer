/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.usecase.bookshelf

import com.sorrowblue.comicviewer.domain.model.bookshelf.Bookshelf
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.common.Resource
import com.sorrowblue.comicviewer.domain.repository.BookshelfRepository
import com.sorrowblue.comicviewer.domain.usecase.UseCase
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * UseCase to observe a single [Bookshelf] by [BookshelfId].
 */
abstract class FlowBookshelfUseCase :
    UseCase<BookshelfId, Bookshelf?, FlowBookshelfUseCase.Error>() {

    sealed interface Error : Resource.AppError
}

@Inject
@ContributesBinding(AppScope::class)
internal class FlowBookshelfUseCaseImpl(private val bookshelfRepository: BookshelfRepository) :
    FlowBookshelfUseCase() {

    override fun run(request: BookshelfId): Flow<Resource<Bookshelf?, Error>> =
        bookshelfRepository.flow(request).map {
            Resource.Success(it)
        }
}
