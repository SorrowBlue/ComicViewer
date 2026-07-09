/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.data.reader.zip.impl

import com.sorrowblue.comicviewer.data.storage.client.FileReader
import com.sorrowblue.comicviewer.data.storage.client.FileReaderFactory
import com.sorrowblue.comicviewer.data.storage.client.SeekableInputStream
import kotlinx.io.Sink
import kotlinx.io.Source

internal expect class ZipFileReader : FileReader {

    fun interface Factory : FileReaderFactory {
        override fun create(seekableInputStream: SeekableInputStream): ZipFileReader
    }

    override suspend fun pageCount(): Int

    override suspend fun source(pageIndex: Int): Source

    override suspend fun extractTo(pageIndex: Int, sink: Sink)

    override suspend fun fileSize(pageIndex: Int): Long

    override suspend fun fileName(pageIndex: Int): String

    override fun close()
}
