/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.usecase.file

import com.sorrowblue.comicviewer.domain.model.bookshelf.Bookshelf
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.common.Resource
import com.sorrowblue.comicviewer.domain.model.file.FileAttribute
import com.sorrowblue.comicviewer.domain.model.storage.RemoteException
import com.sorrowblue.comicviewer.domain.repository.BookshelfRepository
import com.sorrowblue.comicviewer.domain.repository.storage.RemoteStorageClient
import com.sorrowblue.comicviewer.domain.usecase.UseCase
import com.sorrowblue.comicviewer.domain.usecase.runCatchingCancellable
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import kotlin.coroutines.cancellation.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

/**
 * Observes the [FileAttribute] of a file on remote storage by its bookshelf ID and path.
 */
abstract class GetFileAttributeUseCase :
    UseCase<GetFileAttributeUseCase.Request, FileAttribute, GetFileAttributeUseCase.Error>() {

    /**
     * Request parameters for [GetFileAttributeUseCase].
     *
     * @property bookshelfId The ID of the bookshelf containing the file.
     * @property path The path of the file.
     */
    data class Request(val bookshelfId: BookshelfId, val path: String)

    /**
     * Errors that can occur when retrieving file attributes.
     */
    sealed interface Error : Resource.AppError {

        /**
         * Indicates that the bookshelf or file attribute was not found.
         */
        data object NotFound : Error

        /**
         * Indicates that a system or remote error occurred.
         */
        data object System : Error
    }
}

@Inject
@ContributesBinding(AppScope::class)
internal class GetFileAttributeUseCaseImpl(
    private val bookshelfRepository: BookshelfRepository,
    private val remoteStorageClientFactory: RemoteStorageClient.Factory,
) : GetFileAttributeUseCase() {

    override fun run(request: Request): Flow<Resource<FileAttribute, Error>> =
        bookshelfRepository.flow(request.bookshelfId)
            .distinctUntilChanged()
            .map<Bookshelf?, Resource<FileAttribute, Error>> { bookshelf ->
                if (bookshelf != null) {
                    runCatchingCancellable {
                        remoteStorageClientFactory.create(bookshelf).getAttribute(request.path)
                    }.fold(
                        onSuccess = { attribute ->
                            if (attribute != null) {
                                Resource.Success(attribute)
                            } else {
                                Resource.Error(Error.NotFound)
                            }
                        },
                        onFailure = {
                            when (it) {
                                is RemoteException.NotFound -> Resource.Error(Error.NotFound)
                                else -> Resource.Error(Error.System)
                            }
                        },
                    )
                } else {
                    Resource.Error(Error.NotFound)
                }
            }.catch { e ->
                if (e is CancellationException) throw e
                emit(Resource.Error(Error.System))
            }
}
