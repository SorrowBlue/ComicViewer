package com.sorrowblue.comicviewer.data.storage.client

import com.sorrowblue.comicviewer.domain.model.bookshelf.Bookshelf
import com.sorrowblue.comicviewer.domain.model.file.Book
import com.sorrowblue.comicviewer.domain.model.file.BookFile
import com.sorrowblue.comicviewer.domain.model.file.BookFolder
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.model.file.FileAttribute
import com.sorrowblue.comicviewer.domain.reader.FileReader
import java.io.InputStream

interface FileClient {

    val bookshelf: Bookshelf

    suspend fun listFiles(
        file: File,
        resolveImageFolder: Boolean = false,
    ): List<File>

    suspend fun exists(path: String): Boolean

    suspend fun current(path: String): File

    suspend fun inputStream(file: File): InputStream

    suspend fun seekableInputStream(file: File): SeekableInputStream

    interface Factory<T : Bookshelf> {
        fun create(bookshelfModel: T): FileClient
    }

    suspend fun connect(path: String)
    suspend fun getAttribute(path: String): FileAttribute?

    suspend fun fileReader(book: Book): FileReader? {
        return when (book) {
            is BookFile -> fileReaderFactory.create(book.extension, seekableInputStream(book))
            is BookFolder -> ImageFolderFileReader(this, book)
        }
    }

    val fileReaderFactory: FileReaderFactory
}
