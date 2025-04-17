package com.sorrowblue.comicviewer.data.storage.client

import com.sorrowblue.comicviewer.domain.model.bookshelf.Bookshelf
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.model.file.FileAttribute
import okio.BufferedSource
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.parameter.parametersOf
import org.koin.core.qualifier.qualifier

interface FileClient<T : Bookshelf> {

    val bookshelf: T

    suspend fun listFiles(
        file: File,
        resolveImageFolder: Boolean = false,
    ): List<File>

    suspend fun exists(path: String): Boolean

    suspend fun current(path: String, resolveImageFolder: Boolean = false): File

    suspend fun bufferedSource(file: File): BufferedSource

    suspend fun seekableInputStream(file: File): SeekableInputStream

    interface Factory<T : Bookshelf> {
        fun create(bookshelfModel: T): FileClient<T>
    }

    suspend fun connect(path: String)
    suspend fun attribute(path: String): FileAttribute
}

inline fun <reified Q, T : Bookshelf> KoinComponent.fileClient(bookshelf: Bookshelf): FileClient<T> {
    return get<FileClient<T>>(qualifier<Q>()) { parametersOf(bookshelf) }
}
