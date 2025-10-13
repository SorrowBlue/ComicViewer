package com.sorrowblue.comicviewer.data.storage.client.impl

import com.sorrowblue.comicviewer.domain.model.bookshelf.Bookshelf
import com.sorrowblue.comicviewer.domain.service.datasource.RemoteDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import jakarta.inject.Singleton

@Singleton
internal class RemoteDataSourceFactory : RemoteDataSource.Factory {
    override fun create(bookshelf: Bookshelf): RemoteDataSource {
        return RemoteDataSourceImpl(bookshelf, Dispatchers.IO)
    }
}
