package com.sorrowblue.comicviewer.data.storage.client

import com.sorrowblue.comicviewer.domain.service.FileReader
import dev.zacsweers.metro.MapKey

interface FileReaderFactory {
    fun create(mimeType: String, seekableInputStream: SeekableInputStream): FileReader
}

@MapKey
annotation class FileReaderKey(val value: FileReaderType)

enum class FileReaderType {
    Zip,
    Document,
}
