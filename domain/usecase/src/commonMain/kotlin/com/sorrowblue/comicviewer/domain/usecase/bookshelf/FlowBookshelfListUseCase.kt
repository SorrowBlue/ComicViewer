/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.usecase.bookshelf

import com.sorrowblue.comicviewer.domain.EmptyRequest
import com.sorrowblue.comicviewer.domain.model.bookshelf.Bookshelf
import com.sorrowblue.comicviewer.domain.model.common.Resource
import com.sorrowblue.comicviewer.domain.model.common.fold
import com.sorrowblue.comicviewer.domain.repository.BookshelfRepository
import com.sorrowblue.comicviewer.domain.usecase.UseCase
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

@Inject
class FlowBookshelfListUseCase(private val bookshelfRepository: BookshelfRepository) :
    UseCase<EmptyRequest, List<Bookshelf>, FlowBookshelfListUseCase.Error>() {

    sealed interface Error : Resource.AppError {
        data object System : Error
    }

    override fun run(request: EmptyRequest): Flow<Resource<List<Bookshelf>, Error>> =
        bookshelfRepository.allBookshelf().fold({ flow ->
            flow.map {
                Resource.Success(it)
            }
        }, {
            flowOf(Resource.Error(Error.System))
        })
}
