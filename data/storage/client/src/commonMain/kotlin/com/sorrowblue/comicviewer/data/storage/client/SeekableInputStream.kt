package com.sorrowblue.comicviewer.data.storage.client

interface SeekableInputStream : AutoCloseable {
    interface Factory<T> {

        fun create(bookshelfModel: T, path: String): SeekableInputStream
    }

    companion object {
        var SEEK_SET = 0
        var SEEK_CUR = 1
        var SEEK_END = 2
    }

    fun read(buf: ByteArray): Int

    fun seek(offset: Long, whence: Int): Long

    fun position(): Long
    fun length(): Long
    fun read(): Int
    fun read(b: ByteArray, offset: Int, length: Int): Int
    fun get(position: Long, bytes: ByteArray?, off: Int, len: Int): Int
    fun get(position: Long): Int
    fun isClosed(): Boolean
    fun isEOF(): Boolean
}
