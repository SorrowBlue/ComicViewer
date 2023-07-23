package com.sorrowblue.comicviewer.data.storage.impl

import com.sorrowblue.comicviewer.data.common.FileModel
import com.sorrowblue.comicviewer.data.common.bookshelf.BookshelfModel
import com.sorrowblue.comicviewer.data.datasource.RemoteDataSource
import com.sorrowblue.comicviewer.data.exception.RemoteException
import com.sorrowblue.comicviewer.data.storage.ImageFolderFileReader
import com.sorrowblue.comicviewer.data.storage.client.FileClientException
import com.sorrowblue.comicviewer.data.storage.client.FileClientFactory
import com.sorrowblue.comicviewer.data.storage.client.FileReaderException
import com.sorrowblue.comicviewer.data.remote.reader.FileReader
import com.sorrowblue.comicviewer.data.remote.reader.FileReaderFactory
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.delay
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

val mutexs = List(10) { Mutex() }

private suspend fun <R> withLock(action: suspend () -> R): R {
    while (true) {
        delay(300)
        mutexs.firstOrNull { !it.isLocked }?.withLock {
            return action.invoke()
        }
    }
}

internal class RemoteDataSourceImpl @AssistedInject constructor(
    fileClientFactory: FileClientFactory,
    private val fileReaderFactory: FileReaderFactory,
    @Assisted private val bookshelfModel: BookshelfModel
) : RemoteDataSource {

    @AssistedFactory
    interface Factory : RemoteDataSource.Factory {
        override fun create(bookshelfModel: BookshelfModel): RemoteDataSourceImpl
    }

    private val fileClient = fileClientFactory.create(bookshelfModel)

    override suspend fun connect(path: String) {
        withLock {
            kotlin.runCatching {
                fileClient.connect(path)
            }.getOrElse {
                throw when (it) {
                    is FileClientException -> when (it) {
                        FileClientException.InvalidAuth -> RemoteException.InvalidAuth
                        FileClientException.InvalidPath -> RemoteException.NotFound
                        FileClientException.InvalidServer -> RemoteException.InvalidServer
                        FileClientException.NoNetwork -> RemoteException.NoNetwork
                    }
                    else -> RemoteException.Unknown
                }
            }
        }
    }

    override suspend fun listFiles(
        fileModel: FileModel,
        resolveImageFolder: Boolean,
        filter: (FileModel) -> Boolean
    ): List<FileModel> {
        return withLock {
            runCatching {
                fileClient.listFiles(fileModel, resolveImageFolder).filter(filter)
            }.getOrElse {
                throw when (it) {
                    is FileClientException -> when (it) {
                        FileClientException.InvalidAuth -> RemoteException.InvalidAuth
                        FileClientException.InvalidPath -> RemoteException.NotFound
                        FileClientException.InvalidServer -> RemoteException.InvalidServer
                        FileClientException.NoNetwork -> RemoteException.NoNetwork
                    }

                    else -> it
                }
            }
        }
    }

    override suspend fun fileModel(path: String): FileModel {
        return withLock {
            runCatching {
                fileClient.current(path)
            }.getOrElse {
                throw when (it) {
                    is FileClientException -> when (it) {
                        FileClientException.InvalidAuth -> RemoteException.InvalidAuth
                        FileClientException.InvalidPath -> RemoteException.NotFound
                        FileClientException.InvalidServer -> RemoteException.InvalidServer
                        FileClientException.NoNetwork -> RemoteException.NoNetwork
                    }

                    else -> it
                }
            }
        }
    }

    override suspend fun exists(path: String): Boolean {
        return withLock {
            runCatching {
                fileClient.exists(path)
            }.getOrElse {
                throw when (it) {
                    is FileClientException -> when (it) {
                        FileClientException.InvalidAuth -> RemoteException.InvalidAuth
                        FileClientException.InvalidPath -> RemoteException.NotFound
                        FileClientException.InvalidServer -> RemoteException.InvalidServer
                        FileClientException.NoNetwork -> RemoteException.NoNetwork
                    }

                    else -> it
                }
            }
        }
    }

    override suspend fun fileReader(fileModel: FileModel): FileReader? {
        return withLock {
            runCatching {
                if (fileModel is FileModel.ImageFolder) {
                    ImageFolderFileReader(fileClient, fileModel)
                } else {
                    fileReaderFactory.create(
                        fileModel.extension,
                        fileClient.seekableInputStream(fileModel)
                    )
                }
            }.getOrElse {
                throw when (it) {
                    is FileClientException -> when (it) {
                        FileClientException.InvalidAuth -> RemoteException.InvalidAuth
                        FileClientException.InvalidPath -> RemoteException.NotFound
                        FileClientException.InvalidServer -> RemoteException.InvalidServer
                        FileClientException.NoNetwork -> RemoteException.NoNetwork
                    }

                    is FileReaderException -> when (it) {
                        FileReaderException.NotSupport -> TODO()
                    }

                    else -> it
                }
            }
        }
    }
}
