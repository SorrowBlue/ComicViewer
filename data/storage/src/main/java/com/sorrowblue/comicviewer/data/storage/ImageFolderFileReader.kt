package com.sorrowblue.comicviewer.data.storage

import com.sorrowblue.comicviewer.data.model.FileModel
import com.sorrowblue.comicviewer.data.model.SUPPORTED_IMAGE
import com.sorrowblue.comicviewer.data.reader.FileReader
import com.sorrowblue.comicviewer.data.storage.client.FileClient
import java.io.InputStream
import kotlinx.coroutines.runBlocking

internal class ImageFolderFileReader(
    private val fileClient: FileClient,
    private val fileModel: FileModel
) : FileReader {

    private val list = runBlocking {
        fileClient.listFiles(fileModel, false)
            .filter { it is FileModel.File && it.extension in SUPPORTED_IMAGE }
            .sortedWith(compareBy<FileModel> { it.name.length }.thenBy { it.name })
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
