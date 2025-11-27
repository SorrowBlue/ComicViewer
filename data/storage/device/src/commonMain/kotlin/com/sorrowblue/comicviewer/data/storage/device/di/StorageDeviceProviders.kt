package com.sorrowblue.comicviewer.data.storage.device.di

import com.sorrowblue.comicviewer.data.storage.client.FileClient
import com.sorrowblue.comicviewer.data.storage.client.FileClientKey
import com.sorrowblue.comicviewer.data.storage.client.FileClientType
import com.sorrowblue.comicviewer.data.storage.device.impl.DeviceFileClient
import com.sorrowblue.comicviewer.data.storage.device.impl.ShareFileClient
import com.sorrowblue.comicviewer.framework.common.scope.DataScope
import dev.zacsweers.metro.Binds
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.IntoMap

@ContributesTo(DataScope::class)
interface StorageDeviceProviders {
    @Binds
    @IntoMap
    @FileClientKey(FileClientType.Device)
    private val DeviceFileClient.Factory.bind: FileClient.Factory<*> get() = this

    @Binds
    @IntoMap
    @FileClientKey(FileClientType.Share)
    private val ShareFileClient.Factory.bind: FileClient.Factory<*> get() = this
}
