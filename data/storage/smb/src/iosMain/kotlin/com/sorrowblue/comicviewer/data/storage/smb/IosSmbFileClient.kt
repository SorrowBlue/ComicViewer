package com.sorrowblue.comicviewer.data.storage.smb

import com.sorrowblue.comicviewer.data.storage.client.SeekableInputStream
import com.sorrowblue.comicviewer.domain.model.bookshelf.SmbServer
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.model.file.FileAttribute
import kotlinx.io.Buffer
import kotlinx.io.Source
import kotlinx.io.bytestring.toByteString
import kotlinx.io.write
import platform.Foundation.NSData

interface IosSmbFileClient {
    suspend fun connect(path: String)
    suspend fun current(path: String): IosSmbFile
    suspend fun listDirectory(path: String): List<IosSmbFile>
    suspend fun seekableInputStream(file: File): SeekableInputStream
    suspend fun source(file: File): Source
    suspend fun attribute(path: String): FileAttribute
    suspend fun fileSize(path: String): Long

    interface Factory {
        fun create(bookshelf: SmbServer): IosSmbFileClient
    }

    companion object {
        lateinit var factory: Factory
    }
}

fun NSData.toSource(): Source {
    val buffer = Buffer()
    buffer.write(toByteString())
    return buffer
}
