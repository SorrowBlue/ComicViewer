package com.sorrowblue.comicviewer.data.storage.client.impl

import com.sorrowblue.comicviewer.data.storage.client.FileClient
import com.sorrowblue.comicviewer.domain.model.SUPPORTED_IMAGE
import com.sorrowblue.comicviewer.domain.model.SortUtil
import com.sorrowblue.comicviewer.domain.model.file.BookFile
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.service.FileReader
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import okio.BufferedSink
import okio.use

internal class ImageFolderFileReader(
    private val dispatcher: CoroutineDispatcher,
    private val fileClient: FileClient<*>,
    private val file: File,
) : FileReader {

    private var list: List<File>? = null

    private suspend fun list(): List<File> {
        return withContext(dispatcher) {
            list ?: fileClient.listFiles(file, false)
                .filter { it is BookFile && it.extension in SUPPORTED_IMAGE }
                .sortedWith(SortUtil.compareFile)
                .also { list = it }
        }
    }

    override suspend fun copyTo(pageIndex: Int, bufferedSink: BufferedSink) {
        withContext(dispatcher) {
            fileClient.bufferedSource(list()[pageIndex]).use {
                it.readAll(bufferedSink)
            }
        }
    }

    override suspend fun fileName(pageIndex: Int): String {
        return withContext(dispatcher) {
            list()[pageIndex].path
        }
    }

    override suspend fun fileSize(pageIndex: Int): Long {
        return withContext(dispatcher) {
            list()[pageIndex].size
        }
    }

    override suspend fun pageCount(): Int {
        return withContext(dispatcher) {
            list().size
        }
    }

    override fun close() {
        // There is nothing to close because it is a folder.
    }
}
