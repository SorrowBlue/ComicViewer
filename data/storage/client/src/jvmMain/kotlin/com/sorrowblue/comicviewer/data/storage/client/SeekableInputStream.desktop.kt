package com.sorrowblue.comicviewer.data.storage.client

actual interface SeekableInputStream : AutoCloseable {
    fun read(buf: ByteArray): Int

    fun seek(offset: Long, whence: Int): Long

    fun position(): Long

    fun length(): Long

    fun read(): Int

    fun read(b: ByteArray, offset: Int, length: Int): Int

    fun isClosed(): Boolean

    fun isEOF(): Boolean

    companion object {
        const val SEEK_SET = 0
        const val SEEK_CUR = 1
        const val SEEK_END = 2
    }
}
