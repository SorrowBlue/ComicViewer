package com.sorrowblue.comicviewer.data.reader.zip.impl

import com.sorrowblue.comicviewer.data.storage.client.SeekableInputStream
import kotlinx.io.Sink
import kotlinx.io.Source
import kotlinx.io.bytestring.toByteString
import kotlinx.io.write
import platform.Foundation.NSData

interface IosZipFileReader {

    fun close()

    suspend fun pageCount(): Int

    suspend fun source(pageIndex: Int): Source

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

fun Sink.writeData(data: NSData) {
    this.write(data.toByteString())
}
