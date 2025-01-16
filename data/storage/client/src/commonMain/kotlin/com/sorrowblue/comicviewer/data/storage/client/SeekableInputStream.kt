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
}
