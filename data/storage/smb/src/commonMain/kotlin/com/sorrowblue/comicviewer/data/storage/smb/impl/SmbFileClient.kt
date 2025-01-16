package com.sorrowblue.comicviewer.data.storage.smb.impl

import com.sorrowblue.comicviewer.data.storage.client.FileClient
import com.sorrowblue.comicviewer.domain.model.bookshelf.SmbServer

internal expect class SmbFileClient : FileClient<SmbServer>
