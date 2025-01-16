package com.sorrowblue.comicviewer.data.storage.client.impl

import com.sorrowblue.comicviewer.domain.model.bookshelf.Bookshelf
import com.sorrowblue.comicviewer.domain.model.file.Book
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.model.file.FileAttribute
import com.sorrowblue.comicviewer.domain.reader.FileReader
import com.sorrowblue.comicviewer.domain.service.datasource.RemoteDataSource
import di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import org.koin.core.annotation.Factory
import org.koin.core.annotation.InjectedParam
import org.koin.core.annotation.Qualifier
import org.koin.core.annotation.Singleton

@Singleton
internal class RemoteDataSourceFactory : RemoteDataSource.Factory {
    override fun create(bookshelf: Bookshelf): RemoteDataSource {
        return RemoteDataSourceImpl(bookshelf, Dispatchers.IO)
    }
}

@Factory
internal class RemoteDataSourceImpl(
    @InjectedParam private val bookshelf: Bookshelf,
    @Qualifier(IoDispatcher::class) private val dispatcher: CoroutineDispatcher,
) : RemoteDataSource {

    override suspend fun connect(path: String) {
        TODO("Not yet implemented")
    }

    override suspend fun exists(path: String): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun listFiles(
        file: File,
        resolveImageFolder: Boolean,
        filter: (File) -> Boolean,
    ): List<File> {
        TODO("Not yet implemented")
    }

    override suspend fun file(path: String, resolveImageFolder: Boolean): File {
        TODO("Not yet implemented")
    }

    override suspend fun fileReader(book: Book): FileReader? {
        TODO("Not yet implemented")
    }

    override suspend fun getAttribute(path: String): FileAttribute? {
        TODO("Not yet implemented")
    }
}
