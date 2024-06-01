package com.sorrowblue.comicviewer.data.storage.client

import com.sorrowblue.comicviewer.domain.reader.FileReader

interface FileReaderFactory {
    fun create(seekableInputStream: SeekableInputStream): FileReader
}
