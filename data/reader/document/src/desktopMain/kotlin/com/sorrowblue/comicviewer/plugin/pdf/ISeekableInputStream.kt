package com.sorrowblue.comicviewer.plugin.pdf

internal interface ISeekableInputStream {
    fun read(buf: ByteArray): Int

    fun seek(offset: Long, whence: Int): Long

    fun position(): Long

    fun close()
}
