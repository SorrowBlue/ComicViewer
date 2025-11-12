package com.sorrowblue.comicviewer.data.reader.zip.impl

import com.sorrowblue.comicviewer.data.storage.client.FileReaderFactory
import com.sorrowblue.comicviewer.data.storage.client.SeekableInputStream
import com.sorrowblue.comicviewer.domain.service.FileReader
import okio.BufferedSink

internal expect class ZipFileReader : FileReader {
    fun interface Factory : FileReaderFactory {
        override fun create(
            mimeType: String,
            seekableInputStream: SeekableInputStream,
        ): ZipFileReader
    }

    override suspend fun pageCount(): Int

    override suspend fun copyTo(pageIndex: Int, bufferedSink: BufferedSink)

    override suspend fun fileSize(pageIndex: Int): Long

    override suspend fun fileName(pageIndex: Int): String

    override fun close()
}
