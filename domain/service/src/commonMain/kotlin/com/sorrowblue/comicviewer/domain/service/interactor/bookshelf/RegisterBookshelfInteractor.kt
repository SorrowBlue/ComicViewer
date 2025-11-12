package com.sorrowblue.comicviewer.domain.service.interactor.bookshelf

import com.sorrowblue.comicviewer.domain.model.Resource
import com.sorrowblue.comicviewer.domain.model.bookshelf.Bookshelf
import com.sorrowblue.comicviewer.domain.model.file.BookFolder
import com.sorrowblue.comicviewer.domain.model.file.Folder
import com.sorrowblue.comicviewer.domain.model.file.IFolder
import com.sorrowblue.comicviewer.domain.service.datasource.BookshelfLocalDataSource
import com.sorrowblue.comicviewer.domain.service.datasource.FileLocalDataSource
import com.sorrowblue.comicviewer.domain.service.datasource.ImageCacheDataSource
import com.sorrowblue.comicviewer.domain.service.datasource.RemoteDataSource
import com.sorrowblue.comicviewer.domain.service.datasource.RemoteException
import com.sorrowblue.comicviewer.domain.usecase.bookshelf.RegisterBookshelfUseCase
import dev.zacsweers.metro.Inject
import logcat.asLog
import logcat.logcat

@Inject
internal class RegisterBookshelfInteractor(
    private val fileLocalDataSource: FileLocalDataSource,
    private val bookshelfLocalDataSource: BookshelfLocalDataSource,
    private val remoteDataSourceFactory: RemoteDataSource.Factory,
    private val imageCacheDataSource: ImageCacheDataSource,
) : RegisterBookshelfUseCase() {
    override suspend fun run(request: Request): Resource<Bookshelf, Error> = runCatching {
        remoteDataSourceFactory.create(request.bookshelf).connect(request.path)
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
                remoteDataSourceFactory.create(request.bookshelf).file(request.path)
            }.fold({ file ->
                if (file is IFolder) {
                    val root = fileLocalDataSource.root(request.bookshelf.id)
                    if (root != null && root.path != file.path) {
                        // 別の本棚を登録する場合、一旦削除
                        imageCacheDataSource.deleteThumbnails(
                            fileLocalDataSource.getCacheKeyList(request.bookshelf.id),
                        )
                        fileLocalDataSource.deleteAll2(request.bookshelf.id)
                    }
                    val bookshelf =
                        bookshelfLocalDataSource.updateOrCreate(
                            request.bookshelf,
                        ) { bookshelf ->
                            val folder =
                                remoteDataSourceFactory
                                    .create(bookshelf)
                                    .file(request.path) as IFolder
                            val folderModel = when (folder) {
                                is BookFolder -> folder.copy(parent = "")

                                is Folder -> folder.copy(parent = "")
                            }
                            fileLocalDataSource.addUpdate(folderModel)
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
