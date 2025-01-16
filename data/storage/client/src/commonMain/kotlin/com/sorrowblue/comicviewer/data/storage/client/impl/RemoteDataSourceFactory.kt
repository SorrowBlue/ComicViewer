package com.sorrowblue.comicviewer.data.storage.client.impl

import com.sorrowblue.comicviewer.domain.model.bookshelf.Bookshelf
import com.sorrowblue.comicviewer.domain.service.datasource.RemoteDataSource
import di.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import org.koin.core.annotation.Singleton

@Singleton(binds = [RemoteDataSource.Factory::class])
internal class RemoteDataSourceFactory @Inject constructor() : RemoteDataSource.Factory {
    override fun create(bookshelf: Bookshelf): RemoteDataSource {
        return RemoteDataSourceImpl(bookshelf, Dispatchers.IO)
    }
}
