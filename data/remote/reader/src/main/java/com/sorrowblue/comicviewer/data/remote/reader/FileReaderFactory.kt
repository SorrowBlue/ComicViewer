package com.sorrowblue.comicviewer.data.remote.reader

import com.sorrowblue.comicviewer.data.common.FileModel
import com.sorrowblue.comicviewer.data.remote.client.FileClient

interface FileReaderFactory {
    suspend fun create(fileClient: FileClient, fileModel: FileModel): FileReader
}
