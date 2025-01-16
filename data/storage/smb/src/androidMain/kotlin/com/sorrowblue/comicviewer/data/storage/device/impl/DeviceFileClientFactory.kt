package com.sorrowblue.comicviewer.data.storage.device.impl

import com.sorrowblue.comicviewer.data.storage.client.FileClient
import com.sorrowblue.comicviewer.data.storage.client.fileClient
import com.sorrowblue.comicviewer.data.storage.client.qualifier.DeviceFileClient
import com.sorrowblue.comicviewer.domain.model.bookshelf.InternalStorage
import org.koin.core.component.KoinComponent

// TODO dagger用なので削除
internal class DeviceFileClientFactory() :
    FileClient.Factory<InternalStorage>,
    KoinComponent {
    override fun create(bookshelfModel: InternalStorage): FileClient<InternalStorage> {
        return fileClient<DeviceFileClient, InternalStorage>(bookshelfModel)
    }
}
