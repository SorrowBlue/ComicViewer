package com.sorrowblue.comicviewer.data.storage.client

actual interface SeekableInputStream : AutoCloseable {
    fun read(buf: ByteArray): Int

    fun seek(offset: Long, whence: Int): Long

    fun position(): Long

    companion object {
        const val SeekSet = 0
        const val SeekCur = 1
        const val SeekEnd = 2
    }
}
