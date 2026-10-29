/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.data.storage.client.impl

import com.sorrowblue.comicviewer.data.storage.client.FileClient
import com.sorrowblue.comicviewer.domain.model.common.IoDispatcher
import com.sorrowblue.comicviewer.domain.model.file.BookFile
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.model.file.SUPPORTED_IMAGE
import com.sorrowblue.comicviewer.domain.repository.file.BookFileReader
import com.sorrowblue.comicviewer.domain.service.file.FileSortService
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import kotlin.concurrent.Volatile
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlinx.io.Sink
import kotlinx.io.Source

@AssistedInject
internal class ImageFolderFileReader(
    @Assisted private val fileClient: FileClient<*>,
    @Assisted private val file: File,
    @IoDispatcher private val dispatcher: CoroutineDispatcher,
    private val fileSortService: FileSortService,
) : BookFileReader {

    @AssistedFactory
    fun interface Factory {
        fun create(fileClient: FileClient<*>, file: File): ImageFolderFileReader
    }

    private val mutex = Mutex()

    @Volatile
    private var list: List<File>? = null

    private suspend fun list(): List<File> = list ?: mutex.withLock {
        list ?: withContext(dispatcher) {
            fileClient
                .listFiles(file, false)
                .filter { it is BookFile && it.extension in SUPPORTED_IMAGE }
                .sortedWith(fileSortService.compareFile)
                .also { list = it }
        }
    }

    override suspend fun source(pageIndex: Int): Source = withContext(dispatcher) {
        fileClient.source(list()[pageIndex])
    }

    override suspend fun extractTo(pageIndex: Int, sink: Sink) {
        withContext(dispatcher) {
            fileClient.source(list()[pageIndex]).use {
                sink.transferFrom(it)
            }
        }
    }

    override suspend fun fileName(pageIndex: Int): String = withContext(dispatcher) {
        list()[pageIndex].path
    }

    override suspend fun fileSize(pageIndex: Int): Long = withContext(dispatcher) {
        list()[pageIndex].size
    }

    override suspend fun pageCount(): Int = withContext(dispatcher) {
        list().size
    }

    override fun close() {
        // There is nothing to close because it is a folder.
    }
}
