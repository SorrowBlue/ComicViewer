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
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

internal class RegisterBookshelfInteractor @Inject constructor(
    private val fileLocalDataSource: FileLocalDataSource,
    private val bookshelfLocalDataSource: BookshelfLocalDataSource,
    private val remoteDataSourceFactory: RemoteDataSource.Factory,
    private val imageCacheDataSource: ImageCacheDataSource,
) : RegisterBookshelfUseCase() {

    override fun run(request: Request): Flow<Resource<Bookshelf, Error>> {
        return flow {
            runCatching {
                remoteDataSourceFactory.create(request.bookshelf).connect(request.path)
            }.onFailure {
                emit(
                    when (it as RemoteException) {
                        RemoteException.InvalidAuth -> Resource.Error(Error.Auth)
                        RemoteException.InvalidServer -> Resource.Error(Error.Host)
                        RemoteException.NotFound -> Resource.Error(Error.Path)
                        RemoteException.NoNetwork -> Resource.Error(Error.Network)
                        RemoteException.Unknown -> Resource.Error(Error.System)
                    }
                )
            }.onSuccess {
                runCatching {
                    remoteDataSourceFactory.create(request.bookshelf).file(request.path)
                }.fold({ file ->
                    if (file is IFolder) {
                        val root = fileLocalDataSource.root(request.bookshelf.id)
                        if (root != null && root.path != file.path) {
                            // 別の本棚を登録する場合、一旦削除
                            imageCacheDataSource.deleteThumbnails(
                                fileLocalDataSource.getCacheKeyList(request.bookshelf.id)
                            )
                            fileLocalDataSource.deleteAll2(request.bookshelf.id)
                        }
                        val bookshelf = bookshelfLocalDataSource.updateOrCreate(request.bookshelf)!!
                        val folderModel = when (file) {
                            is BookFolder -> file.copy(
                                bookshelfId = bookshelf.id,
                                parent = ""
                            )

                            is Folder -> file.copy(bookshelfId = bookshelf.id, parent = "")
                        }
                        fileLocalDataSource.addUpdate(folderModel)
                        emit(Resource.Success(bookshelf))
                    } else {
                        emit(Resource.Error(Error.Network))
                    }
                }, {
                    emit(Resource.Error(Error.System))
                })
            }
        }
    }
}
