package com.sorrowblue.comicviewer.domain.service.interactor.file

import com.sorrowblue.comicviewer.domain.model.Resource
import com.sorrowblue.comicviewer.domain.model.file.FileAttribute
import com.sorrowblue.comicviewer.domain.service.datasource.BookshelfLocalDataSource
import com.sorrowblue.comicviewer.domain.service.datasource.RemoteDataSource
import com.sorrowblue.comicviewer.domain.usecase.file.GetFileAttributeUseCase
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal class GetFileAttributeInteractor @Inject constructor(
    private val bookshelfLocalDataSource: BookshelfLocalDataSource,
    private val remoteDataSourceFactory: RemoteDataSource.Factory,
) : GetFileAttributeUseCase() {
    override fun run(request: Request): Flow<Resource<FileAttribute, Error>> {
        return bookshelfLocalDataSource.flow(request.bookshelfId).map { bookshelf ->
            if (bookshelf != null) {
                kotlin.runCatching {
                    remoteDataSourceFactory.create(bookshelf).getAttribute(request.path)
                }.fold({ attribute ->
                    if (attribute != null) {
                        Resource.Success(attribute)
                    } else {
                        Resource.Error(Error.NotFound)
                    }
                }, {
                    Resource.Error(Error.System)
                })
            } else {
                Resource.Error(Error.NotFound)
            }
        }
    }
}
