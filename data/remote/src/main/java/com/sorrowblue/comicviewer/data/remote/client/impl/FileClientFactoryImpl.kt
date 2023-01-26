package com.sorrowblue.comicviewer.data.remote.client.impl

import com.sorrowblue.comicviewer.data.common.DeviceStorageModel
import com.sorrowblue.comicviewer.data.common.ServerModel
import com.sorrowblue.comicviewer.data.common.SmbServerModel
import com.sorrowblue.comicviewer.data.remote.client.FileClient
import com.sorrowblue.comicviewer.data.remote.client.FileClientFactory
import com.sorrowblue.comicviewer.data.remote.client.qualifier.DeviceFileClientFactory
import com.sorrowblue.comicviewer.data.remote.client.qualifier.SmbFileClientFactory
import javax.inject.Inject

internal class FileClientFactoryImpl @Inject constructor(
    @DeviceFileClientFactory
    private val deviceFileClientFactory: FileClient.Factory<DeviceStorageModel>,
    @SmbFileClientFactory
    private val smbFileClientFactory: FileClient.Factory<SmbServerModel>
) : FileClientFactory {

    override fun create(serverModel: ServerModel): FileClient {
        return when (serverModel) {
            is DeviceStorageModel -> deviceFileClientFactory.create(serverModel)
            is SmbServerModel -> smbFileClientFactory.create(serverModel)
        }
    }
}
