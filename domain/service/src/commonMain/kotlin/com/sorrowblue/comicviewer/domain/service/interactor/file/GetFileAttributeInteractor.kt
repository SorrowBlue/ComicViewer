/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.service.interactor.file

import com.sorrowblue.comicviewer.domain.model.common.Resource
import com.sorrowblue.comicviewer.domain.model.file.FileAttribute
import com.sorrowblue.comicviewer.domain.repository.BookshelfRepository
import com.sorrowblue.comicviewer.domain.service.storage.RemoteStorageClient
import com.sorrowblue.comicviewer.domain.usecase.file.GetFileAttributeUseCase
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@ContributesBinding(AppScope::class)
internal class GetFileAttributeInteractor(
    private val bookshelfRepository: BookshelfRepository,
    private val remoteStorageClientFactory: RemoteStorageClient.Factory,
) : GetFileAttributeUseCase() {
    override fun run(request: Request): Flow<Resource<FileAttribute, Error>> =
        bookshelfRepository.flow(request.bookshelfId).map { bookshelf ->
            if (bookshelf != null) {
                kotlin
                    .runCatching {
                        remoteStorageClientFactory.create(bookshelf).getAttribute(request.path)
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
