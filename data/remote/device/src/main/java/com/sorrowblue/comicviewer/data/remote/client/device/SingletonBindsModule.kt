package com.sorrowblue.comicviewer.data.remote.client.device

import com.sorrowblue.comicviewer.data.common.bookshelf.BookshelfModel
import com.sorrowblue.comicviewer.data.remote.client.FileClient
import com.sorrowblue.comicviewer.data.remote.client.qualifier.DeviceFileClientFactory
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal abstract class SingletonBindsModule {

    @DeviceFileClientFactory
    @Binds
    abstract fun bindDeviceFileClientFactory(factory: DeviceFileClient.Factory): FileClient.Factory<BookshelfModel.InternalStorage>
}
