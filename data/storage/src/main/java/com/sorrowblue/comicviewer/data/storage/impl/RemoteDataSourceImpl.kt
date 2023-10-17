package com.sorrowblue.comicviewer.data.storage.impl

import com.sorrowblue.comicviewer.data.infrastructure.datasource.RemoteDataSource
import com.sorrowblue.comicviewer.data.infrastructure.exception.RemoteException
import com.sorrowblue.comicviewer.data.reader.FileReader
import com.sorrowblue.comicviewer.data.reader.FileReaderFactory
import com.sorrowblue.comicviewer.data.storage.ImageFolderFileReader
import com.sorrowblue.comicviewer.data.storage.client.FileClientException
import com.sorrowblue.comicviewer.data.storage.client.FileClientFactory
import com.sorrowblue.comicviewer.data.storage.client.FileReaderException
import com.sorrowblue.comicviewer.domain.model.bookshelf.Bookshelf
import com.sorrowblue.comicviewer.domain.model.file.Book
import com.sorrowblue.comicviewer.domain.model.file.BookFile
import com.sorrowblue.comicviewer.domain.model.file.BookFolder
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
    @Assisted private val bookshelf: Bookshelf,
) : RemoteDataSource {

    @AssistedFactory
    interface Factory : RemoteDataSource.Factory {
        override fun create(bookshelf: Bookshelf): RemoteDataSourceImpl
    }

    private val fileClient = fileClientFactory.create(bookshelf)

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
        file: com.sorrowblue.comicviewer.domain.model.file.File,
        resolveImageFolder: Boolean,
        filter: (com.sorrowblue.comicviewer.domain.model.file.File) -> Boolean,
    ): List<com.sorrowblue.comicviewer.domain.model.file.File> {
        return withLock {
            runCatching {
                fileClient.listFiles(file, resolveImageFolder).filter(filter)
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

    override suspend fun file(path: String): com.sorrowblue.comicviewer.domain.model.file.File {
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

    override suspend fun fileReader(book: Book): FileReader? {
        return withLock {
            runCatching {
                when (book) {
                    is BookFile -> fileReaderFactory.create(
                        book.extension,
                        fileClient.seekableInputStream(book)
                    )

                    is BookFolder -> ImageFolderFileReader(fileClient, book)
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
