package com.sorrowblue.comicviewer.data.datasource

import com.sorrowblue.comicviewer.data.common.FileModel
import com.sorrowblue.comicviewer.data.common.bookshelf.BookshelfModel
import com.sorrowblue.comicviewer.data.exception.RemoteException
import com.sorrowblue.comicviewer.data.remote.reader.FileReader

interface RemoteDataSource {

    interface Factory {
        fun create(bookshelfModel: BookshelfModel): RemoteDataSource
    }

    @Throws(RemoteException::class)
    suspend fun connect(path: String)

    @Throws(RemoteException::class)
    suspend fun exists(path: String): Boolean

    @Throws(RemoteException::class)
    suspend fun listFiles(
        fileModel: FileModel,
        resolveImageFolder: Boolean = false,
        filter: (FileModel) -> Boolean
    ): List<FileModel>

    @Throws(RemoteException::class)
    suspend fun fileModel(path: String): FileModel

    suspend fun fileReader(fileModel: FileModel): FileReader
}
