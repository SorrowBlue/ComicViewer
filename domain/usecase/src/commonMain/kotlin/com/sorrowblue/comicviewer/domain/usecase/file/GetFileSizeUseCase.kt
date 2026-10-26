/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.usecase.file

import com.sorrowblue.comicviewer.domain.model.bookshelf.Bookshelf
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.common.Resource
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
import kotlinx.coroutines.flow.map

abstract class GetFileSizeUseCase :
    UseCase<GetFileSizeUseCase.Request, Long, GetFileSizeUseCase.Error>() {

    data class Request(val bookshelfId: BookshelfId, val path: String)

    sealed interface Error : Resource.AppError {
        data object NotFound : Error
        data object System : Error
    }
}

@Inject
@ContributesBinding(AppScope::class)
internal class GetFileSizeUseCaseImpl(
    private val bookshelfRepository: BookshelfRepository,
    private val remoteStorageClientFactory: RemoteStorageClient.Factory,
) : GetFileSizeUseCase() {

    override fun run(request: Request): Flow<Resource<Long, Error>> =
        bookshelfRepository.flow(request.bookshelfId)
            .map<Bookshelf?, Resource<Long, Error>> { bookshelf ->
                if (bookshelf != null) {
                    runCatchingCancellable {
                        remoteStorageClientFactory.create(bookshelf).getFileSize(request.path)
                    }.fold(
                        onSuccess = { size ->
                            Resource.Success(size)
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
