package com.sorrowblue.comicviewer.data.remote.client

import com.sorrowblue.comicviewer.data.common.FileModel
import com.sorrowblue.comicviewer.data.common.ServerModel
import com.sorrowblue.comicviewer.data.remote.reader.SeekableInputStream
import java.io.InputStream

interface FileClient {

    val serverModel: ServerModel

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

    interface Factory<T : ServerModel> {
        fun create(serverModel: T): FileClient
    }

    suspend fun connect(path: String)
}
