package com.sorrowblue.comicviewer.domain.service.interactor.bookshelf

import com.sorrowblue.comicviewer.domain.EmptyRequest
import com.sorrowblue.comicviewer.domain.model.Resource
import com.sorrowblue.comicviewer.domain.model.bookshelf.Bookshelf
import com.sorrowblue.comicviewer.domain.model.fold
import com.sorrowblue.comicviewer.domain.service.datasource.BookshelfLocalDataSource
import com.sorrowblue.comicviewer.domain.usecase.bookshelf.FlowBookshelfListUseCase
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

@ContributesBinding(AppScope::class)
internal class FlowBookshelfListInteractor(
    private val bookshelfLocalDataSource: BookshelfLocalDataSource,
) : FlowBookshelfListUseCase() {
    override fun run(request: EmptyRequest): Flow<Resource<List<Bookshelf>, Error>> =
        bookshelfLocalDataSource.allBookshelf().fold({ flow ->
            flow.map {
                Resource.Success(it)
            }
        }, {
            flowOf(Resource.Error(Error.System))
        })
}
