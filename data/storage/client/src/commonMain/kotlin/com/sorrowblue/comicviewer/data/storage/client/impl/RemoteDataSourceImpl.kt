package com.sorrowblue.comicviewer.data.storage.client.impl

import com.sorrowblue.comicviewer.data.storage.client.FileClientException
import com.sorrowblue.comicviewer.data.storage.client.FileReaderException
import com.sorrowblue.comicviewer.data.storage.client.fileClient
import com.sorrowblue.comicviewer.data.storage.client.qualifier.DeviceFileClient
import com.sorrowblue.comicviewer.data.storage.client.qualifier.DocumentFileReader
import com.sorrowblue.comicviewer.data.storage.client.qualifier.ShareFileClient
import com.sorrowblue.comicviewer.data.storage.client.qualifier.SmbFileClient
import com.sorrowblue.comicviewer.data.storage.client.qualifier.ZipFileReader
import com.sorrowblue.comicviewer.domain.model.bookshelf.Bookshelf
import com.sorrowblue.comicviewer.domain.model.bookshelf.InternalStorage
import com.sorrowblue.comicviewer.domain.model.bookshelf.ShareContents
import com.sorrowblue.comicviewer.domain.model.bookshelf.SmbServer
import com.sorrowblue.comicviewer.domain.model.file.Book
import com.sorrowblue.comicviewer.domain.model.file.BookFile
import com.sorrowblue.comicviewer.domain.model.file.BookFolder
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.model.file.FileAttribute
import com.sorrowblue.comicviewer.domain.reader.FileReader
import com.sorrowblue.comicviewer.domain.service.datasource.RemoteDataSource
import com.sorrowblue.comicviewer.domain.service.datasource.RemoteException
import com.sorrowblue.comicviewer.domain.service.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import logcat.asLog
import logcat.logcat
import org.koin.core.annotation.Factory
import org.koin.core.annotation.InjectedParam
import org.koin.core.annotation.Qualifier
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.parameter.parametersOf
import org.koin.core.qualifier.qualifier

@Factory(binds = [RemoteDataSource::class])
internal class RemoteDataSourceImpl(
    @InjectedParam private val bookshelf: Bookshelf,
    @Qualifier(IoDispatcher::class) private val dispatcher: CoroutineDispatcher,
) : RemoteDataSource, KoinComponent {

    private val fileClient = when (bookshelf) {
        is InternalStorage -> fileClient<DeviceFileClient, InternalStorage>(bookshelf)
        is SmbServer -> fileClient<SmbFileClient, SmbServer>(bookshelf)
        ShareContents -> fileClient<ShareFileClient, ShareContents>(bookshelf)
    }

    override suspend fun connect(path: String) {
        kotlin.runCatching {
            withContext(dispatcher) {
                fileClient.connect(path)
            }
        }.getOrElse {
            throw when (it) {
                is FileClientException -> when (it) {
                    is FileClientException.InvalidAuth -> RemoteException.InvalidAuth()
                    is FileClientException.InvalidPath -> RemoteException.NotFound()
                    is FileClientException.InvalidServer -> RemoteException.InvalidServer()
                    is FileClientException.NoNetwork -> RemoteException.NoNetwork()
                }

                else -> RemoteException.Unknown()
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
                    is FileClientException.InvalidAuth -> RemoteException.InvalidAuth()
                    is FileClientException.InvalidPath -> RemoteException.NotFound()
                    is FileClientException.InvalidServer -> RemoteException.InvalidServer()
                    is FileClientException.NoNetwork -> RemoteException.NoNetwork()
                }

                else -> it
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
                    is FileClientException.InvalidAuth -> RemoteException.InvalidAuth()
                    is FileClientException.InvalidPath -> RemoteException.NotFound()
                    is FileClientException.InvalidServer -> RemoteException.InvalidServer()
                    is FileClientException.NoNetwork -> RemoteException.NoNetwork()
                }

                else -> it
            }
        }
    }

    override suspend fun file(path: String, resolveImageFolder: Boolean): File {
        return runCatching {
            withContext(dispatcher) {
                fileClient.current(path, resolveImageFolder)
            }
        }.getOrElse {
            throw when (it) {
                is FileClientException -> when (it) {
                    is FileClientException.InvalidAuth -> RemoteException.InvalidAuth()
                    is FileClientException.InvalidPath -> RemoteException.NotFound()
                    is FileClientException.InvalidServer -> RemoteException.InvalidServer()
                    is FileClientException.NoNetwork -> RemoteException.NoNetwork()
                }

                else -> it
            }
        }
    }

    override suspend fun fileReader(book: Book): FileReader {
        return withContext(dispatcher) {
            kotlin.runCatching {
                when (book) {
                    is BookFile -> {
                        val seekableInputStream = fileClient.seekableInputStream(book)
                        when (book.extension) {
                            "pdf", "epub", "xps", "oxps", "mobi", "fb2" ->
                                get<FileReader>(qualifier<DocumentFileReader>()) {
                                    parametersOf(book.extension, seekableInputStream)
                                }

                            else -> get<FileReader>(qualifier<ZipFileReader>()) {
                                parametersOf(seekableInputStream)
                            }
                        }
                    }

                    is BookFolder -> ImageFolderFileReader(dispatcher, fileClient, book)
                }
            }.getOrElse {
                logcat { it.asLog() }
                throw when (it) {
                    is FileClientException -> when (it) {
                        is FileClientException.InvalidAuth -> RemoteException.InvalidAuth()
                        is FileClientException.InvalidPath -> RemoteException.NotFound()
                        is FileClientException.InvalidServer -> RemoteException.InvalidServer()
                        is FileClientException.NoNetwork -> RemoteException.NoNetwork()
                    }

                    is FileReaderException -> when (it) {
                        is FileReaderException.NotSupport -> RemoteException.NotFound()
                    }

                    else -> it
                }
            }
        }
    }

    override suspend fun getAttribute(path: String): FileAttribute? {
        return kotlin.runCatching {
            withContext(dispatcher) {
                fileClient.attribute(path = path)
            }
        }.getOrElse {
            throw when (it) {
                is FileClientException -> when (it) {
                    is FileClientException.InvalidAuth -> RemoteException.InvalidAuth()
                    is FileClientException.InvalidPath -> RemoteException.NotFound()
                    is FileClientException.InvalidServer -> RemoteException.InvalidServer()
                    is FileClientException.NoNetwork -> RemoteException.NoNetwork()
                }

                else -> RemoteException.Unknown()
            }
        }
    }
}
