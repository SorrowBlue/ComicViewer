package com.sorrowblue.comicviewer.data.reader.zip.impl

import com.sorrowblue.comicviewer.data.storage.client.SeekableInputStream
import okio.BufferedSink
import okio.ByteString.Companion.toByteString
import platform.Foundation.NSData

interface IosZipFileReader {

    fun close()

    suspend fun pageCount(): Int

    suspend fun copyTo(pageIndex: Int, bufferedSink: BufferedSink)

    suspend fun fileSize(pageIndex: Int): Long

    suspend fun fileName(pageIndex: Int): String

    interface Factory {
        @Throws(Throwable::class)
        fun create(
            seekableInputStream: SeekableInputStream,
            supportedExtension: Set<String>,
        ): IosZipFileReader
    }

    companion object {
        lateinit var factory: Factory
    }
}

fun BufferedSink.writeData(data: NSData) {
    this.write(data.toByteString())
}
