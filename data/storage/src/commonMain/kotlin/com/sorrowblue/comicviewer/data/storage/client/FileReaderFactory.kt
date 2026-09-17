/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.data.storage.client

import com.sorrowblue.comicviewer.domain.repository.file.BookFileReader
import dev.zacsweers.metro.MapKey

typealias FileReaderFactoryMap = Map<FileReaderType, FileReaderFactory>

interface FileReaderFactory {
    fun create(seekableInputStream: SeekableInputStream): BookFileReader
}

@MapKey
annotation class FileReaderKey(val value: FileReaderType)

enum class FileReaderType {
    Zip,
    Document,
}
