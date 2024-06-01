package com.sorrowblue.comicviewer.data.storage.client.impl

import com.sorrowblue.comicviewer.data.storage.client.FileClient
import com.sorrowblue.comicviewer.domain.model.SUPPORTED_IMAGE
import com.sorrowblue.comicviewer.domain.model.file.BookFile
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.reader.FileReader
import okio.Sink
import okio.buffer

internal class ImageFolderFileReader(
    private val fileClient: FileClient,
    private val file: File,
) : FileReader {

    private var list: List<File>? = null

    private suspend fun list(): List<File> {
        return list ?: fileClient.listFiles(file, false)
            .filter { it is BookFile && it.extension in SUPPORTED_IMAGE }
            .sortedWith(compareBy<File> { it.name.length }.thenBy { it.name }).also { list = it }
    }

    override suspend fun copyTo(pageIndex: Int, sink: Sink) {
        sink.buffer().outputStream().use {
            fileClient.inputStream(list()[pageIndex]).copyTo(it)
        }
    }

    override suspend fun fileName(pageIndex: Int): String {
        return list()[pageIndex].path
    }

    override suspend fun fileSize(pageIndex: Int): Long {
        return list()[pageIndex].size
    }

    override suspend fun pageCount(): Int {
        return list().size
    }

    override fun close() {
        // There is nothing to close because it is a folder.
    }
}
