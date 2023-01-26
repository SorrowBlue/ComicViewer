package com.sorrowblue.comicviewer.data.remote.reader

interface FileReaderFactory {
    suspend fun create(extension: String, seekableInputStream: SeekableInputStream): FileReader
}
