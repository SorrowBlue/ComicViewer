package com.sorrowblue.comicviewer.data.storage.client

import dev.zacsweers.metro.MapKey

typealias FileReaderFactoryMap = Map<FileReaderType, FileReaderFactory>

interface FileReaderFactory {
    fun create(mimeType: String, seekableInputStream: SeekableInputStream): FileReader
}

@MapKey
annotation class FileReaderKey(val value: FileReaderType)

enum class FileReaderType {
    Zip,
    Document,
}
