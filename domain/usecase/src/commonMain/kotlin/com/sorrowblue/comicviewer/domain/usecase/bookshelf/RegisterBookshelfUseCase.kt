/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.usecase.bookshelf

import com.sorrowblue.comicviewer.domain.model.bookshelf.Bookshelf
import com.sorrowblue.comicviewer.domain.model.common.Resource
import com.sorrowblue.comicviewer.domain.model.file.BookFolder
import com.sorrowblue.comicviewer.domain.model.file.Folder
import com.sorrowblue.comicviewer.domain.model.file.IFolder
import com.sorrowblue.comicviewer.domain.model.storage.RemoteException
import com.sorrowblue.comicviewer.domain.repository.BookshelfRepository
import com.sorrowblue.comicviewer.domain.repository.FileRepository
import com.sorrowblue.comicviewer.domain.repository.ImageCacheRepository
import com.sorrowblue.comicviewer.domain.repository.storage.RemoteStorageClient
import com.sorrowblue.comicviewer.domain.usecase.OneShotUseCase
import com.sorrowblue.comicviewer.domain.usecase.runCatchingCancellable
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import logcat.asLog
import logcat.logcat

abstract class RegisterBookshelfUseCase :
    OneShotUseCase<RegisterBookshelfUseCase.Request, Bookshelf, RegisterBookshelfUseCase.Error>() {

    data class Request(val bookshelf: Bookshelf, val path: String)

    sealed interface Error : Resource.AppError {
        data object Host : Error

        data object Path : Error

        data object Auth : Error

        data object Network : Error

        data object System : Error
    }
}

@Inject
@ContributesBinding(AppScope::class)
internal class RegisterBookshelfUseCaseImpl(
    private val fileRepository: FileRepository,
    private val bookshelfRepository: BookshelfRepository,
    private val remoteStorageClientFactory: RemoteStorageClient.Factory,
    private val imageCacheRepository: ImageCacheRepository,
) : RegisterBookshelfUseCase() {

    override suspend fun run(request: Request): Resource<Bookshelf, Error> {
        val client = remoteStorageClientFactory.create(request.bookshelf)
        return runCatchingCancellable {
            client.connect(request.path)
            when (val file = client.file(request.path)) {
                is IFolder -> saveBookshelf(request.bookshelf, file)
                else -> Resource.Error(Error.Path)
            }
        }.getOrElse {
            logcat { "onFailure ${it.asLog()}" }
            Resource.Error(mapError(it))
        }
    }

    private suspend fun saveBookshelf(
        targetBookshelf: Bookshelf,
        file: IFolder,
    ): Resource<Bookshelf, Error> {
        val root = fileRepository.root(targetBookshelf.id)
        if (root != null && root.path != file.path) {
            // 別の本棚を登録する場合、一旦削除
            imageCacheRepository.deleteThumbnails(
                fileRepository.getCacheKeyList(targetBookshelf.id),
            )
            fileRepository.deleteAll(targetBookshelf.id)
        }
        val bookshelf = bookshelfRepository.updateOrCreate(targetBookshelf) { created ->
            val folderModel = when (file) {
                is BookFolder -> file.copy(
                    bookshelfId = created.id,
                    parent = "",
                )

                is Folder -> file.copy(
                    bookshelfId = created.id,
                    parent = "",
                )
            }
            fileRepository.addUpdate(folderModel)
        }
        return if (bookshelf != null) {
            Resource.Success(bookshelf)
        } else {
            Resource.Error(Error.System)
        }
    }

    private fun mapError(throwable: Throwable): Error = when (throwable) {
        is RemoteException.InvalidAuth -> Error.Auth
        is RemoteException.InvalidServer -> Error.Host
        is RemoteException.NotFound -> Error.Path
        is RemoteException.NoNetwork -> Error.Network
        is RemoteException.Unknown -> Error.System
        else -> Error.System
    }
}
