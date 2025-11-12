package com.sorrowblue.comicviewer.domain.service.datasource

import com.sorrowblue.comicviewer.domain.model.bookshelf.Bookshelf
import com.sorrowblue.comicviewer.domain.model.file.Book
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.model.file.FileAttribute
import com.sorrowblue.comicviewer.domain.service.FileReader
import kotlin.coroutines.cancellation.CancellationException

interface RemoteDataSource {
    interface Factory {
        fun create(bookshelf: Bookshelf): RemoteDataSource
    }

    @Throws(RemoteException::class, CancellationException::class)
    suspend fun connect(path: String)

    @Throws(RemoteException::class, CancellationException::class)
    suspend fun exists(path: String): Boolean

    @Throws(RemoteException::class, CancellationException::class)
    suspend fun listFiles(
        file: File,
        resolveImageFolder: Boolean = false,
        filter: (File) -> Boolean,
    ): List<File>

    @Throws(RemoteException::class, CancellationException::class)
    suspend fun file(path: String, resolveImageFolder: Boolean = false): File

    suspend fun fileReader(book: Book): FileReader?

    suspend fun getAttribute(path: String): FileAttribute?
}
