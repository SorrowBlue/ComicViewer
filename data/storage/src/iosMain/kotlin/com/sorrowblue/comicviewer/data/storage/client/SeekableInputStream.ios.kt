package com.sorrowblue.comicviewer.data.storage.client

import kotlinx.cinterop.COpaquePointer
import kotlinx.cinterop.ExperimentalForeignApi

actual interface SeekableInputStream : AutoCloseable {

    /**
     * Swift UnsafeMutableRawPointer?
     */
    @OptIn(ExperimentalForeignApi::class)
    fun read(pointer: COpaquePointer, count: Int): Int

    fun seek(offset: Long, whence: Int): Long

    fun position(): Long

    fun size(): Long

    fun open()

    companion object {
        const val SEEK_SET = 0
        const val SEEK_CUR = 1
        const val SEEK_END = 2
    }
}
