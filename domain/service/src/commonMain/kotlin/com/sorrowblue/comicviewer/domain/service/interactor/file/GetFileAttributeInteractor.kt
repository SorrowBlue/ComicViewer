package com.sorrowblue.comicviewer.domain.service.interactor.file

import com.sorrowblue.comicviewer.domain.model.Resource
import com.sorrowblue.comicviewer.domain.model.file.FileAttribute
import com.sorrowblue.comicviewer.domain.service.datasource.BookshelfLocalDataSource
import com.sorrowblue.comicviewer.domain.service.datasource.RemoteDataSource
import com.sorrowblue.comicviewer.domain.usecase.file.GetFileAttributeUseCase
import com.sorrowblue.comicviewer.framework.common.scope.DataScope
import dev.zacsweers.metro.ContributesBinding
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@ContributesBinding(DataScope::class)
internal class GetFileAttributeInteractor(
    private val bookshelfLocalDataSource: BookshelfLocalDataSource,
    private val remoteDataSourceFactory: RemoteDataSource.Factory,
) : GetFileAttributeUseCase() {
    override fun run(request: Request): Flow<Resource<FileAttribute, Error>> =
        bookshelfLocalDataSource.flow(request.bookshelfId).map { bookshelf ->
            if (bookshelf != null) {
                kotlin
                    .runCatching {
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
