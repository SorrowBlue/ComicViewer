package com.sorrowblue.comicviewer.domain.service.interactor.bookshelf

import com.sorrowblue.comicviewer.domain.EmptyRequest
import com.sorrowblue.comicviewer.domain.model.Resource
import com.sorrowblue.comicviewer.domain.model.bookshelf.Bookshelf
import com.sorrowblue.comicviewer.domain.model.fold
import com.sorrowblue.comicviewer.domain.service.datasource.BookshelfLocalDataSource
import com.sorrowblue.comicviewer.domain.usecase.bookshelf.FlowBookshelfListUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import org.koin.core.annotation.Factory

@Factory
internal class FlowBookshelfListInteractor(
    private val bookshelfLocalDataSource: BookshelfLocalDataSource,
) : FlowBookshelfListUseCase() {
    override fun run(request: EmptyRequest): Flow<Resource<List<Bookshelf>, Error>> {
        // Resource<Flow<List<Bookshelf>>, Resource.SystemError>
        return bookshelfLocalDataSource.allBookshelf().fold({
            it.map {
                Resource.Success(it)
            }
        }, {
            flowOf(Resource.Error(Error.System))
        })
    }
}
