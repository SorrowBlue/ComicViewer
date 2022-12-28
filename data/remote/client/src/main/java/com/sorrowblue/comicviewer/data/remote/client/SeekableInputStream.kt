package com.sorrowblue.comicviewer.data.remote.client

import java.io.Closeable

interface SeekableInputStream : Closeable {

    companion object {
        var SEEK_SET = 0
        var SEEK_CUR = 1
        var SEEK_END = 2
    }

    fun read(buf: ByteArray): Int

    fun seek(offset: Long, whence: Int): Long

    fun position(): Long
}
