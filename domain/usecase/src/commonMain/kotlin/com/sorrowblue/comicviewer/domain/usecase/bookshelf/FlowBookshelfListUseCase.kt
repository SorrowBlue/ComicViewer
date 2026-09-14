/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.usecase.bookshelf

import com.sorrowblue.comicviewer.domain.model.bookshelf.Bookshelf
import com.sorrowblue.comicviewer.domain.model.common.Resource
import com.sorrowblue.comicviewer.domain.model.common.fold
import com.sorrowblue.comicviewer.domain.repository.BookshelfRepository
import com.sorrowblue.comicviewer.domain.usecase.UseCase
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

abstract class FlowBookshelfListUseCase :
    UseCase<Unit, List<Bookshelf>, FlowBookshelfListUseCase.Error>() {

    operator fun invoke(): Flow<Resource<List<Bookshelf>, Error>> = invoke(Unit)

    sealed interface Error : Resource.AppError {
        data object System : Error
    }
}

@Inject
@ContributesBinding(AppScope::class)
internal class FlowBookshelfListUseCaseImpl(private val bookshelfRepository: BookshelfRepository) :
    FlowBookshelfListUseCase() {

    override fun run(request: Unit): Flow<Resource<List<Bookshelf>, Error>> =
        bookshelfRepository.allBookshelf().fold({ flow ->
            flow.map {
                Resource.Success(it)
            }
        }, {
            flowOf(Resource.Error(Error.System))
        })
}
