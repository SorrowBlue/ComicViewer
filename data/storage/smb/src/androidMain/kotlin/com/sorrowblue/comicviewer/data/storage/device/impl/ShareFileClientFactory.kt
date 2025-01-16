package com.sorrowblue.comicviewer.data.storage.device.impl

import com.sorrowblue.comicviewer.data.storage.client.FileClient
import com.sorrowblue.comicviewer.data.storage.client.fileClient
import com.sorrowblue.comicviewer.data.storage.client.qualifier.ShareFileClient
import com.sorrowblue.comicviewer.domain.model.bookshelf.ShareContents
import org.koin.core.component.KoinComponent

// TODO dagger用なので削除
internal class ShareFileClientFactory() :
    FileClient.Factory<ShareContents>,
    KoinComponent {
    override fun create(bookshelfModel: ShareContents): FileClient<ShareContents> {
        return fileClient<ShareFileClient, ShareContents>(bookshelfModel)
    }
}
