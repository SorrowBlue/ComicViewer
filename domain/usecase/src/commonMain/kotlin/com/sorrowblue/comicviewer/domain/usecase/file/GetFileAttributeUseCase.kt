/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.usecase.file

import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.common.Resource
import com.sorrowblue.comicviewer.domain.model.file.FileAttribute
import com.sorrowblue.comicviewer.domain.repository.BookshelfRepository
import com.sorrowblue.comicviewer.domain.repository.storage.RemoteStorageClient
import com.sorrowblue.comicviewer.domain.usecase.UseCase
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Inject
class GetFileAttributeUseCase(
    private val bookshelfRepository: BookshelfRepository,
    private val remoteStorageClientFactory: RemoteStorageClient.Factory,
) : UseCase<GetFileAttributeUseCase.Request, FileAttribute, GetFileAttributeUseCase.Error>() {

    sealed interface Error : Resource.AppError {
        data object NotFound : Error

        data object System : Error
    }

    data class Request(val bookshelfId: BookshelfId, val path: String) : UseCase.Request

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
