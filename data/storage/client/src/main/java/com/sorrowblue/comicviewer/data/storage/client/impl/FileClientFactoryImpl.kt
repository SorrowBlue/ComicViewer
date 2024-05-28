package com.sorrowblue.comicviewer.data.storage.client.impl

import com.sorrowblue.comicviewer.data.storage.client.FileClient
import com.sorrowblue.comicviewer.data.storage.client.FileClientFactory
import com.sorrowblue.comicviewer.data.storage.client.qualifier.DeviceFileClientFactory
import com.sorrowblue.comicviewer.data.storage.client.qualifier.SmbFileClientFactory
import com.sorrowblue.comicviewer.domain.model.bookshelf.Bookshelf
import com.sorrowblue.comicviewer.domain.model.bookshelf.InternalStorage
import com.sorrowblue.comicviewer.domain.model.bookshelf.SmbServer
import javax.inject.Inject

internal class FileClientFactoryImpl @Inject constructor(
    @DeviceFileClientFactory
    private val deviceFileClientFactory: FileClient.Factory<InternalStorage>,
    @SmbFileClientFactory
    private val smbFileClientFactory: FileClient.Factory<SmbServer>,
) : FileClientFactory {

    override fun create(bookshelf: Bookshelf): FileClient {
        return when (bookshelf) {
            is InternalStorage -> deviceFileClientFactory.create(bookshelf)
            is SmbServer -> smbFileClientFactory.create(bookshelf)
        }
    }
}
