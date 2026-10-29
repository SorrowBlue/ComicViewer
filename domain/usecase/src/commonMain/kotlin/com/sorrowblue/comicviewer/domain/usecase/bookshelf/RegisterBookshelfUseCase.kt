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

/**
 * Validates connectivity and registers or updates a [Bookshelf] along with its root folder.
 */
abstract class RegisterBookshelfUseCase :
    OneShotUseCase<RegisterBookshelfUseCase.Request, Bookshelf, RegisterBookshelfUseCase.Error>() {

    /**
     * Request parameters for [RegisterBookshelfUseCase].
     *
     * @property bookshelf The bookshelf configuration to register or update.
     * @property path The root folder path on the storage.
     */
    data class Request(val bookshelf: Bookshelf, val path: String)

    /**
     * Errors that can occur when registering a bookshelf.
     */
    sealed interface Error : Resource.AppError {
        /**
         * Indicates that the remote server or host could not be reached.
         */
        data object Host : Error

        /**
         * Indicates that the specified path does not exist or is not a folder.
         */
        data object Path : Error

        /**
         * Indicates that authentication failed.
         */
        data object Auth : Error

        /**
         * Indicates that a network connectivity error occurred.
         */
        data object Network : Error

        /**
         * Indicates that an unexpected system error occurred.
         */
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
        val isRootPathChanged = root != null && root.path != file.path
        val oldCacheKeys = if (isRootPathChanged) {
            fileRepository.getCacheKeyList(targetBookshelf.id)
        } else {
            emptyList()
        }
        val bookshelf = bookshelfRepository.updateOrCreate(targetBookshelf) { created ->
            if (isRootPathChanged) {
                // 別の本棚を登録する場合、一旦削除
                fileRepository.deleteAll(targetBookshelf.id)
            }
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
            if (oldCacheKeys.isNotEmpty()) {
                imageCacheRepository.deleteThumbnails(oldCacheKeys)
            }
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
