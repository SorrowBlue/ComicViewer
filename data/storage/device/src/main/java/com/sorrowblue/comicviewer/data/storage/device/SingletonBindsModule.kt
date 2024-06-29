package com.sorrowblue.comicviewer.data.storage.device

import com.sorrowblue.comicviewer.data.storage.client.FileClient
import com.sorrowblue.comicviewer.data.storage.client.qualifier.DeviceFileClientFactory
import com.sorrowblue.comicviewer.data.storage.client.qualifier.ShareFileClientFactory
import com.sorrowblue.comicviewer.domain.model.bookshelf.InternalStorage
import com.sorrowblue.comicviewer.domain.model.bookshelf.ShareContents
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal interface SingletonBindsModule {

    @DeviceFileClientFactory
    @Binds
    fun bindDeviceFileClientFactory(factory: DeviceFileClient.Factory): FileClient.Factory<InternalStorage>

    @ShareFileClientFactory
    @Binds
    fun bindShareFileClientFactory(factory: ShareFileClient.Factory): FileClient.Factory<ShareContents>
}
