package com.sorrowblue.comicviewer.domain.service.datasource

import com.sorrowblue.comicviewer.domain.model.bookshelf.Bookshelf
import com.sorrowblue.comicviewer.domain.model.file.Book
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.model.file.FileAttribute
import com.sorrowblue.comicviewer.domain.reader.FileReader

interface RemoteDataSource {

    interface Factory {
        fun create(bookshelf: Bookshelf): RemoteDataSource
    }

    @Throws(RemoteException::class)
    suspend fun connect(path: String)

    @Throws(RemoteException::class)
    suspend fun exists(path: String): Boolean

    @Throws(RemoteException::class)
    suspend fun listFiles(
        file: File,
        resolveImageFolder: Boolean = false,
        filter: (File) -> Boolean,
    ): List<File>

    @Throws(RemoteException::class)
    suspend fun file(path: String): File

    suspend fun fileReader(book: Book): FileReader?
    suspend fun getAttribute(path: String): FileAttribute?
}
