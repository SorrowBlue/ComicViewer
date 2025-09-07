package com.sorrowblue.comicviewer.data.storage.smb.impl

import com.sorrowblue.comicviewer.data.storage.client.FileClient
import com.sorrowblue.comicviewer.data.storage.client.qualifier.SmbFileClient
import com.sorrowblue.comicviewer.domain.model.bookshelf.SmbServer
import org.koin.core.annotation.Factory
import org.koin.core.annotation.InjectedParam

@SmbFileClient
@Factory
internal expect fun providesSmbFileClient(@InjectedParam bookshelf: SmbServer): FileClient<SmbServer>
