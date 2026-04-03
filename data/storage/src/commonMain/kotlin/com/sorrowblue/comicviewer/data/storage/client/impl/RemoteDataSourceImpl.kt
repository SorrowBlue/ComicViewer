package com.sorrowblue.comicviewer.data.storage.client.impl

import com.sorrowblue.comicviewer.data.storage.client.FileClient
import com.sorrowblue.comicviewer.data.storage.client.FileClientException
import com.sorrowblue.comicviewer.data.storage.client.FileClientType
import com.sorrowblue.comicviewer.domain.model.bookshelf.Bookshelf
import com.sorrowblue.comicviewer.domain.model.bookshelf.DeviceStorage
import com.sorrowblue.comicviewer.domain.model.bookshelf.ShareContents
import com.sorrowblue.comicviewer.domain.model.bookshelf.SmbServer
import com.sorrowblue.comicviewer.domain.model.file.Book
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.model.file.FileAttribute
import com.sorrowblue.comicviewer.domain.service.datasource.RemoteDataSource
import com.sorrowblue.comicviewer.domain.service.datasource.RemoteException
import com.sorrowblue.comicviewer.framework.common.IoDispatcher
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import logcat.asLog
import logcat.logcat

@AssistedInject
internal class RemoteDataSourceImpl(
    @Assisted private val bookshelf: Bookshelf,
    @IoDispatcher private val dispatcher: CoroutineDispatcher,
    fileClientFactory: Map<FileClientType, FileClient.Factory<*>>,
) : RemoteDataSource {
    @AssistedFactory
    fun interface Factory : RemoteDataSource.Factory {
        override fun create(bookshelf: Bookshelf): RemoteDataSourceImpl
    }

    @Suppress("UNCHECKED_CAST")
    private val fileClient = when (bookshelf) {
        is DeviceStorage -> fileClientFactory.getValue(
            FileClientType.Device,
        ) as FileClient.Factory<Bookshelf>

        is SmbServer -> fileClientFactory.getValue(
            FileClientType.Smb,
        ) as FileClient.Factory<Bookshelf>

        ShareContents -> fileClientFactory.getValue(
            FileClientType.Share,
        ) as FileClient.Factory<Bookshelf>
    }.create(bookshelf)

    override suspend fun connect(path: String) {
        kotlin
            .runCatching {
                withContext(dispatcher) {
                    fileClient.connect(path)
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

                    else -> RemoteException.Unknown()
                }
            }
    }

    override suspend fun exists(path: String): Boolean = runCatching {
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

    override suspend fun listFiles(
        file: File,
        resolveImageFolder: Boolean,
        filter: (File) -> Boolean,
    ): List<File> = runCatching {
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

    override suspend fun file(path: String, resolveImageFolder: Boolean): File = runCatching {
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

    override suspend fun pageCount(book: Book): Int = fileClient.fileReader(book).use {
        it.pageCount()
    }

    override suspend fun getAttribute(path: String): FileAttribute = kotlin.runCatching {
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

    override suspend fun getFileSize(path: String): Long = kotlin.runCatching {
        withContext(dispatcher) {
            fileClient.fileSize(path = path)
        }
    }.getOrDefault(-1)
}
