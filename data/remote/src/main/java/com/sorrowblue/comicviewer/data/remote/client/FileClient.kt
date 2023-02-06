package com.sorrowblue.comicviewer.data.remote.client

import com.sorrowblue.comicviewer.data.common.FileModel
import com.sorrowblue.comicviewer.data.common.bookshelf.BookshelfModel
import com.sorrowblue.comicviewer.data.remote.reader.SeekableInputStream
import java.io.InputStream

interface FileClient {

    val bookshelfModel: BookshelfModel

    suspend fun listFiles(
        fileModel: FileModel,
        resolveImageFolder: Boolean = false
    ): List<FileModel>

    suspend fun exists(fileModel: FileModel): Boolean
    suspend fun exists(path: String): Boolean

    suspend fun current(path: String): FileModel

    suspend fun current(fileModel: FileModel): FileModel

    suspend fun inputStream(fileModel: FileModel): InputStream

    suspend fun seekableInputStream(fileModel: FileModel): SeekableInputStream

    interface Factory<T : BookshelfModel> {
        fun create(bookshelfModel: T): FileClient
    }

    suspend fun connect(path: String)
}
