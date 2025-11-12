package com.sorrowblue.comicviewer.data.storage.client

import com.sorrowblue.comicviewer.domain.model.bookshelf.Bookshelf
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.model.file.FileAttribute
import dev.zacsweers.metro.MapKey
import okio.BufferedSource

@MapKey
annotation class FileClientKey(val value: FileClientType)

enum class FileClientType {
    Smb,
    Device,
    Share,
}

interface FileClient<T : Bookshelf> {
    interface Factory<T : Bookshelf> {
        fun create(bookshelf: T): FileClient<T>
    }

    val bookshelf: T

    suspend fun listFiles(file: File, resolveImageFolder: Boolean = false): List<File>

    suspend fun exists(path: String): Boolean

    suspend fun current(path: String, resolveImageFolder: Boolean = false): File

    suspend fun bufferedSource(file: File): BufferedSource

    suspend fun seekableInputStream(file: File): SeekableInputStream

    suspend fun connect(path: String)

    suspend fun attribute(path: String): FileAttribute
}
