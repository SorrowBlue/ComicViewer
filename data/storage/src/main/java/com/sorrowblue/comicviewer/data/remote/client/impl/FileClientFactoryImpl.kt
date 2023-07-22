package com.sorrowblue.comicviewer.data.remote.client.impl

import com.sorrowblue.comicviewer.data.common.bookshelf.BookshelfModel
import com.sorrowblue.comicviewer.data.remote.client.FileClient
import com.sorrowblue.comicviewer.data.remote.client.FileClientFactory
import com.sorrowblue.comicviewer.data.remote.client.qualifier.DeviceFileClientFactory
import com.sorrowblue.comicviewer.data.remote.client.qualifier.SmbFileClientFactory
import javax.inject.Inject

internal class FileClientFactoryImpl @Inject constructor(
    @DeviceFileClientFactory
    private val deviceFileClientFactory: FileClient.Factory<BookshelfModel.InternalStorage>,
    @SmbFileClientFactory
    private val smbFileClientFactory: FileClient.Factory<BookshelfModel.SmbServer>
) : FileClientFactory {

    override fun create(bookshelfModel: BookshelfModel): FileClient {
        return when (bookshelfModel) {
            is BookshelfModel.InternalStorage -> deviceFileClientFactory.create(bookshelfModel)
            is BookshelfModel.SmbServer -> smbFileClientFactory.create(bookshelfModel)
        }
    }
}
