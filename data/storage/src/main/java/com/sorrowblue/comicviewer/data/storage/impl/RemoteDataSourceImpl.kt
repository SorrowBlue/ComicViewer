package com.sorrowblue.comicviewer.data.storage.impl

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
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.model.file.FileAttribute
import com.sorrowblue.comicviewer.domain.service.datasource.RemoteDataSource
import com.sorrowblue.comicviewer.domain.service.datasource.RemoteException
import com.sorrowblue.comicviewer.domain.service.di.IoDispatcher
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

internal class RemoteDataSourceImpl @AssistedInject constructor(
    fileClientFactory: FileClientFactory,
    private val fileReaderFactory: FileReaderFactory,
    @Assisted private val bookshelf: Bookshelf,
    @IoDispatcher private val dispatcher: CoroutineDispatcher,
) : RemoteDataSource {

    @AssistedFactory
    interface Factory : RemoteDataSource.Factory {
        override fun create(bookshelf: Bookshelf): RemoteDataSourceImpl
    }

    private val fileClient = fileClientFactory.create(bookshelf)

    override suspend fun getAttribute(path: String): FileAttribute? {
        return kotlin.runCatching {
            withContext(dispatcher) {
                fileClient.getAttribute(path = path)
            }
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

    override suspend fun connect(path: String) {
        kotlin.runCatching {
            withContext(dispatcher) {
                fileClient.connect(path)
            }
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

    override suspend fun listFiles(
        file: File,
        resolveImageFolder: Boolean,
        filter: (File) -> Boolean,
    ): List<File> {
        return runCatching {
            withContext(dispatcher) {
                fileClient.listFiles(file, resolveImageFolder).filter(filter)
            }
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

    override suspend fun file(path: String): File {
        return runCatching {
            withContext(dispatcher) {
                fileClient.current(path)
            }
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

    override suspend fun exists(path: String): Boolean {
        return runCatching {
            withContext(dispatcher) {
                fileClient.exists(path)
            }
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

    override suspend fun fileReader(book: Book): FileReader? {
        return runCatching {
            withContext(dispatcher) {
                when (book) {
                    is BookFile -> fileReaderFactory.create(
                        book.extension,
                        fileClient.seekableInputStream(book)
                    )

                    is BookFolder -> ImageFolderFileReader(fileClient, book)
                }
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
