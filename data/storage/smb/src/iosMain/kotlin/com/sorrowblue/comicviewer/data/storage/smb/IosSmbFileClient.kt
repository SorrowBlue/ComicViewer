package com.sorrowblue.comicviewer.data.storage.smb

import com.sorrowblue.comicviewer.data.storage.client.SeekableInputStream
import com.sorrowblue.comicviewer.domain.model.bookshelf.SmbServer
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.model.file.FileAttribute
import okio.Buffer
import okio.BufferedSource
import okio.ByteString.Companion.toByteString
import platform.Foundation.NSData

interface IosSmbFileClient {
    suspend fun connect(path: String)
    suspend fun current(path: String): IosSmbFile
    suspend fun listDirectory(path: String): List<IosSmbFile>
    suspend fun seekableInputStream(file: File): SeekableInputStream
    suspend fun bufferedSource(file: File): BufferedSource
    suspend fun attribute(path: String): FileAttribute

    interface Factory {
        fun create(bookshelf: SmbServer): IosSmbFileClient
    }

    companion object {
        lateinit var factory: Factory
    }
}

fun NSData.toBufferedSource(): BufferedSource {
    val buffer = Buffer()
    buffer.write(this.toByteString())
    return buffer
}
