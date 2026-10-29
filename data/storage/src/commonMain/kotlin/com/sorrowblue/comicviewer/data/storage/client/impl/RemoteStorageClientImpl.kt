/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.data.storage.client.impl

import com.sorrowblue.comicviewer.data.storage.client.FileClient
import com.sorrowblue.comicviewer.data.storage.client.FileClientFactory
import com.sorrowblue.comicviewer.data.storage.client.getFileClient
import com.sorrowblue.comicviewer.data.storage.client.toRemoteException
import com.sorrowblue.comicviewer.domain.model.bookshelf.Bookshelf
import com.sorrowblue.comicviewer.domain.model.common.IoDispatcher
import com.sorrowblue.comicviewer.domain.model.file.Book
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.model.file.FileAttribute
import com.sorrowblue.comicviewer.domain.repository.file.BookFileReader
import com.sorrowblue.comicviewer.domain.repository.storage.RemoteStorageClient
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import dev.zacsweers.metro.ContributesBinding
import kotlin.coroutines.cancellation.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import logcat.asLog
import logcat.logcat

@AssistedInject
internal class RemoteStorageClientImpl(
    @Assisted private val bookshelf: Bookshelf,
    @IoDispatcher private val dispatcher: CoroutineDispatcher,
    fileClientFactory: FileClientFactory,
    private val imageFolderFileReaderFactory: ImageFolderFileReader.Factory,
) : RemoteStorageClient {

    @ContributesBinding(AppScope::class)
    @AssistedFactory
    interface Factory : RemoteStorageClient.Factory {
        override fun create(bookshelf: Bookshelf): RemoteStorageClientImpl
    }

    private val fileClient: FileClient<Bookshelf> = fileClientFactory.getFileClient(bookshelf)

    override suspend fun connect(path: String) {
        runRemote(logOnError = true) {
            withContext(dispatcher) {
                fileClient.connect(path)
            }
        }
    }

    override suspend fun exists(path: String): Boolean = runRemote {
        withContext(dispatcher) {
            fileClient.exists(path)
        }
    }

    override suspend fun listFiles(
        file: File,
        resolveImageFolder: Boolean,
        filter: (File) -> Boolean,
    ): List<File> = runRemote {
        withContext(dispatcher) {
            fileClient.listFiles(file, resolveImageFolder).filter(filter)
        }
    }

    override suspend fun file(path: String, resolveImageFolder: Boolean): File = runRemote {
        withContext(dispatcher) {
            fileClient.current(path, resolveImageFolder)
        }
    }

    override suspend fun pageCount(book: Book): Int = runRemote {
        fileClient.fileReader(book, imageFolderFileReaderFactory).use {
            it.pageCount()
        }
    }

    override suspend fun fileReader(book: Book): BookFileReader = runRemote {
        fileClient.fileReader(book, imageFolderFileReaderFactory)
    }

    override suspend fun getAttribute(path: String): FileAttribute = runRemote {
        withContext(dispatcher) {
            fileClient.attribute(path = path)
        }
    }

    override suspend fun getFileSize(path: String): Long = runRemote {
        withContext(dispatcher) {
            fileClient.fileSize(path = path)
        }
    }

    private inline fun <T> runRemote(logOnError: Boolean = false, block: () -> T): T =
        runCatching(block).getOrElse {
            if (it is CancellationException) throw it
            if (logOnError) {
                logcat { it.asLog() }
            }
            throw it.toRemoteException()
        }
}
