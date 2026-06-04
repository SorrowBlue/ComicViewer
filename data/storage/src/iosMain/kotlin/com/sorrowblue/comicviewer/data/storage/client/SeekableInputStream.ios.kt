package com.sorrowblue.comicviewer.data.storage.client

import kotlinx.cinterop.COpaquePointer
import kotlinx.cinterop.ExperimentalForeignApi

interface IosSeekableInputStream : SeekableInputStream {

    /**
     * Swift UnsafeMutableRawPointer?
     */
    @OptIn(ExperimentalForeignApi::class)
    fun read(pointer: COpaquePointer, count: Int): Int

    companion object {
        const val SEEK_SET = 0
        const val SEEK_CUR = 1
        const val SEEK_END = 2
    }
}
