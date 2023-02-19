package com.sorrowblue.comicviewer.library.onedrive.data

import java.io.IOException
import java.io.OutputStream

internal class ProgressOutputStream(private val underlying: OutputStream, private val onProgress: (Long) -> Unit) : OutputStream() {
    private var completed: Long = 0

    override fun write(data: ByteArray, off: Int, len: Int) {
        underlying.write(data, off, len)
        track(len)
    }

    @Throws(IOException::class)
    override fun write(data: ByteArray) {
        underlying.write(data)
        track(data.size)
    }

    @Throws(IOException::class)
    override fun write(c: Int) {
        underlying.write(c)
        track(1)
    }

    @Throws(IOException::class)
    override fun flush() {
        underlying.flush()
    }

    @Throws(IOException::class)
    override fun close() {
        underlying.close()
    }

    private fun track(len: Int) {
        completed += len.toLong()
        onProgress(completed)
    }
}
