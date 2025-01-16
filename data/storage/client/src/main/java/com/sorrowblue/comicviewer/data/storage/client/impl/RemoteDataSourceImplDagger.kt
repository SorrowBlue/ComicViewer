package com.sorrowblue.comicviewer.data.storage.client.impl

import android.content.Context
import com.sorrowblue.comicviewer.data.storage.client.FileClient
import com.sorrowblue.comicviewer.data.storage.client.FileClientException
import com.sorrowblue.comicviewer.data.storage.client.FileReaderException
import com.sorrowblue.comicviewer.data.storage.client.FileReaderFactory
import com.sorrowblue.comicviewer.data.storage.client.FileReaderProvider
import com.sorrowblue.comicviewer.data.storage.client.SeekableInputStream
import com.sorrowblue.comicviewer.data.storage.client.qualifier.DeviceFileClientFactory
import com.sorrowblue.comicviewer.data.storage.client.qualifier.ShareFileClientFactory
import com.sorrowblue.comicviewer.data.storage.client.qualifier.SmbFileClientFactory
import com.sorrowblue.comicviewer.data.storage.client.qualifier.ZipFileReaderFactory
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
import com.sorrowblue.comicviewer.domain.service.di.IoDispatcher
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.ServiceLoader
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

internal class RemoteDataSourceImplDagger @AssistedInject constructor(
    @Assisted private val bookshelf: Bookshelf,
    @IoDispatcher private val dispatcher: CoroutineDispatcher,
    @ApplicationContext private val context: Context,
    @DeviceFileClientFactory private val deviceFileClientFactory: FileClient.Factory<InternalStorage>,
    @ShareFileClientFactory private val shareFileClientFactory: FileClient.Factory<ShareContents>,
    @SmbFileClientFactory private val smbFileClientFactory: FileClient.Factory<SmbServer>,
    @ZipFileReaderFactory private val zipFileReaderFactory: FileReaderFactory,
) : RemoteDataSource {

    @AssistedFactory
    interface Factory : RemoteDataSource.Factory {
        override fun create(bookshelf: Bookshelf): RemoteDataSourceImplDagger
    }

    private val fileClient = when (bookshelf) {
        is InternalStorage -> deviceFileClientFactory.create(bookshelf)
        is SmbServer -> smbFileClientFactory.create(bookshelf)
        ShareContents -> shareFileClientFactory.create(bookshelf as ShareContents)
    }

    override suspend fun getAttribute(path: String): FileAttribute? {
        return kotlin.runCatching {
            withContext(dispatcher) {
                fileClient.getAttribute(path = path)
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

    override suspend fun fileReader(book: Book): FileReader? {
        return withContext(dispatcher) {
            kotlin.runCatching {
                when (book) {
                    is BookFile -> {
                        when (book.extension) {
                            "pdf", "epub", "xps", "oxps", "mobi", "fb2" ->
                                documentReader(
                                    book.extension,
                                    fileClient.seekableInputStream(book),
                                    dispatcher
                                )

                            else -> zipFileReaderFactory.create(
                                fileClient.seekableInputStream(
                                    book
                                )
                            )
                        }
                    }

                    is BookFolder -> ImageFolderFileReader(dispatcher, fileClient, book)
                }
            }.getOrElse {
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

    private fun documentReader(
        extension: String,
        seekableInputStream: SeekableInputStream,
        dispatcher: CoroutineDispatcher,
    ) =
        ServiceLoader.load(
            FileReaderProvider::class.java,
            FileReaderProvider::class.java.classLoader
        ).firstOrNull { it.extension == extension }?.get(context, seekableInputStream, dispatcher)
}
