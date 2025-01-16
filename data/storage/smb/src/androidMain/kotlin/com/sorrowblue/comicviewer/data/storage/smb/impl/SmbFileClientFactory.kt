package com.sorrowblue.comicviewer.data.storage.smb.impl

import com.sorrowblue.comicviewer.data.storage.client.FileClient
import com.sorrowblue.comicviewer.data.storage.client.fileClient
import com.sorrowblue.comicviewer.data.storage.client.qualifier.SmbFileClient
import com.sorrowblue.comicviewer.domain.model.bookshelf.SmbServer
import di.Inject
import org.koin.core.component.KoinComponent

// TODO dagger用なので削除
internal class SmbFileClientFactory @Inject constructor() : FileClient.Factory<SmbServer>,
    KoinComponent {
    override fun create(bookshelfModel: SmbServer): FileClient<SmbServer> {
        return fileClient<SmbFileClient, SmbServer>(bookshelfModel)
    }
}
