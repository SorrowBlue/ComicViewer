/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.service.interactor.bookshelf

import com.sorrowblue.comicviewer.domain.model.bookshelf.Bookshelf
import com.sorrowblue.comicviewer.domain.model.common.Resource
import com.sorrowblue.comicviewer.domain.model.file.BookFolder
import com.sorrowblue.comicviewer.domain.model.file.Folder
import com.sorrowblue.comicviewer.domain.model.file.IFolder
import com.sorrowblue.comicviewer.domain.repository.BookshelfRepository
import com.sorrowblue.comicviewer.domain.repository.FileRepository
import com.sorrowblue.comicviewer.domain.repository.ImageCacheRepository
import com.sorrowblue.comicviewer.domain.service.storage.RemoteException
import com.sorrowblue.comicviewer.domain.service.storage.RemoteStorageClient
import com.sorrowblue.comicviewer.domain.usecase.bookshelf.RegisterBookshelfUseCase
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import logcat.asLog
import logcat.logcat

@ContributesBinding(AppScope::class)
internal class RegisterBookshelfInteractor(
    private val fileRepository: FileRepository,
    private val bookshelfRepository: BookshelfRepository,
    private val remoteStorageClientFactory: RemoteStorageClient.Factory,
    private val imageCacheRepository: ImageCacheRepository,
) : RegisterBookshelfUseCase() {
    override suspend fun run(request: Request): Resource<Bookshelf, Error> = runCatching {
        remoteStorageClientFactory.create(request.bookshelf).connect(request.path)
    }.fold(
        onFailure = {
            logcat { "onFailure ${it.asLog()}" }
            when (it as RemoteException) {
                is RemoteException.InvalidAuth -> Resource.Error(Error.Auth)
                is RemoteException.InvalidServer -> Resource.Error(Error.Host)
                is RemoteException.NotFound -> Resource.Error(Error.Path)
                is RemoteException.NoNetwork -> Resource.Error(Error.Network)
                is RemoteException.Unknown -> Resource.Error(Error.System)
            }
        },
        onSuccess = {
            logcat { "onSuccess" }
            runCatching {
                remoteStorageClientFactory.create(request.bookshelf).file(request.path)
            }.fold({ file ->
                if (file is IFolder) {
                    val root = fileRepository.root(request.bookshelf.id)
                    if (root != null && root.path != file.path) {
                        // 別の本棚を登録する場合、一旦削除
                        imageCacheRepository.deleteThumbnails(
                            fileRepository.getCacheKeyList(request.bookshelf.id),
                        )
                        fileRepository.deleteAll2(request.bookshelf.id)
                    }
                    val bookshelf =
                        bookshelfRepository.updateOrCreate(
                            request.bookshelf,
                        ) { bookshelf ->
                            val folder =
                                remoteStorageClientFactory
                                    .create(bookshelf)
                                    .file(request.path) as IFolder
                            val folderModel = when (folder) {
                                is BookFolder -> folder.copy(parent = "")
                                is Folder -> folder.copy(parent = "")
                            }
                            fileRepository.addUpdate(folderModel)
                        }
                    Resource.Success(requireNotNull(bookshelf))
                } else {
                    Resource.Error(Error.Network)
                }
            }, {
                Resource.Error(Error.System)
            })
        },
    )
}
