package com.sorrowblue.comicviewer.data.storage

import com.sorrowblue.comicviewer.data.reader.FileReader
import com.sorrowblue.comicviewer.data.storage.client.FileClient
import com.sorrowblue.comicviewer.domain.model.SUPPORTED_IMAGE
import com.sorrowblue.comicviewer.domain.model.file.BookFile
import com.sorrowblue.comicviewer.domain.model.file.File
import java.io.InputStream
import kotlinx.coroutines.runBlocking

internal class ImageFolderFileReader(
    private val fileClient: FileClient,
    private val file: File,
) : FileReader {

    private val list = runBlocking {
        fileClient.listFiles(file, false)
            .filter { it is BookFile && it.extension in SUPPORTED_IMAGE }
            .sortedWith(compareBy<File> { it.name.length }.thenBy { it.name })
    }

    override suspend fun pageInputStream(pageIndex: Int): InputStream {
        return fileClient.inputStream(list[pageIndex])
    }

    override fun fileName(pageIndex: Int): String {
        return list[pageIndex].path
    }

    override fun fileSize(pageIndex: Int): Long {
        return list[pageIndex].size
    }

    override fun pageCount(): Int {
        return list.size
    }

    override fun close() {
    }
}
