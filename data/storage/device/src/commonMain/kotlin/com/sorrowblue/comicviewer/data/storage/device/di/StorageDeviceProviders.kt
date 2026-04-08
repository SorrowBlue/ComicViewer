package com.sorrowblue.comicviewer.data.storage.device.di

import com.sorrowblue.comicviewer.data.storage.client.FileClient
import com.sorrowblue.comicviewer.data.storage.client.FileClientKey
import com.sorrowblue.comicviewer.data.storage.device.impl.DeviceFileClient
import com.sorrowblue.comicviewer.data.storage.device.impl.ShareFileClient
import com.sorrowblue.comicviewer.domain.model.bookshelf.DeviceStorage
import com.sorrowblue.comicviewer.domain.model.bookshelf.ShareContents
import com.sorrowblue.comicviewer.framework.common.scope.DataScope
import dev.zacsweers.metro.Binds
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.IntoMap

@ContributesTo(DataScope::class)
interface StorageDeviceProviders {
    @FileClientKey(DeviceStorage::class)
    @IntoMap
    @Binds
    private val DeviceFileClient.Factory.bind: FileClient.Factory<*> get() = this

    @FileClientKey(ShareContents::class)
    @IntoMap
    @Binds
    private val ShareFileClient.Factory.bind: FileClient.Factory<*> get() = this
}
