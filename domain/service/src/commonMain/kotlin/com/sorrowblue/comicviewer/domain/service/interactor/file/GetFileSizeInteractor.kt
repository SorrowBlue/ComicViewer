/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.service.interactor.file

import com.sorrowblue.comicviewer.domain.model.common.Resource
import com.sorrowblue.comicviewer.domain.repository.BookshelfRepository
import com.sorrowblue.comicviewer.domain.service.datasource.RemoteDataSource
import com.sorrowblue.comicviewer.domain.usecase.file.GetFileSizeUseCase
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@ContributesBinding(AppScope::class)
internal class GetFileSizeInteractor(
    private val bookshelfRepository: BookshelfRepository,
    private val remoteDataSourceFactory: RemoteDataSource.Factory,
) : GetFileSizeUseCase() {
    override fun run(request: Request): Flow<Resource<Long, Error>> =
        bookshelfRepository.flow(request.bookshelfId).map { bookshelf ->
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
