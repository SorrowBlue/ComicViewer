package com.sorrowblue.comicviewer.data.reader

interface FileReaderFactory {
    suspend fun create(extension: String, seekableInputStream: SeekableInputStream): FileReader?
}
