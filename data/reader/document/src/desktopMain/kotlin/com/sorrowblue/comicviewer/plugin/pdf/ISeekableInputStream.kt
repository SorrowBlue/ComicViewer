package com.sorrowblue.comicviewer.plugin.pdf

import java.io.OutputStream

interface ISeekableInputStream {

    fun read(buf: ByteArray): Int

    fun seek(offset: Long, whence: Int): Long

    fun position(): Long

    fun close()
}

internal interface FileReader {
    fun pageCount(): Int
    fun loadPage(pageIndex: Int, outputStream: OutputStream)
    fun fileSize(pageIndex: Int): Long

    fun close()
}
