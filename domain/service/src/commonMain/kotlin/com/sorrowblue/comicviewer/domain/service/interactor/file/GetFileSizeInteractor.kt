package com.sorrowblue.comicviewer.domain.service.interactor.file

import com.sorrowblue.comicviewer.domain.model.Resource
import com.sorrowblue.comicviewer.domain.service.datasource.BookshelfLocalDataSource
import com.sorrowblue.comicviewer.domain.service.datasource.RemoteDataSource
import com.sorrowblue.comicviewer.domain.usecase.file.GetFileSizeUseCase
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Inject
internal class GetFileSizeInteractor(
    private val bookshelfLocalDataSource: BookshelfLocalDataSource,
    private val remoteDataSourceFactory: RemoteDataSource.Factory,
) : GetFileSizeUseCase() {
    override fun run(request: Request): Flow<Resource<Long, Error>> =
        bookshelfLocalDataSource.flow(request.bookshelfId).map { bookshelf ->
            if (bookshelf != null) {
                runCatching {
                    remoteDataSourceFactory.create(bookshelf).getFileSize(request.path)
                }.fold({ size ->
                    Resource.Success(size)
                }, {
                    Resource.Error(Error.System)
                })
            } else {
                Resource.Error(Error.NotFound)
            }
        }
}
