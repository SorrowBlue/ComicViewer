package com.sorrowblue.comicviewer.data.storage.device.di

import com.sorrowblue.comicviewer.data.storage.client.FileClient
import com.sorrowblue.comicviewer.data.storage.device.impl.DeviceFileClientFactory
import com.sorrowblue.comicviewer.data.storage.device.impl.ShareFileClientFactory
import com.sorrowblue.comicviewer.domain.model.bookshelf.InternalStorage
import com.sorrowblue.comicviewer.domain.model.bookshelf.ShareContents
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal interface SingletonBindsModule {

    @com.sorrowblue.comicviewer.data.storage.client.qualifier.DeviceFileClientFactory
    @Binds
    fun bindDeviceFileClientFactory(factory: DeviceFileClientFactory): FileClient.Factory<InternalStorage>

    @com.sorrowblue.comicviewer.data.storage.client.qualifier.ShareFileClientFactory
    @Binds
    fun bindShareFileClientFactory(factory: ShareFileClientFactory): FileClient.Factory<ShareContents>
}
