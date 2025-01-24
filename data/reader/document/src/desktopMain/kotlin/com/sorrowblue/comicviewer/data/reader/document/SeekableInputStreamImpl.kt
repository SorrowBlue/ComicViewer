package com.sorrowblue.comicviewer.data.reader.document

import com.sorrowblue.comicviewer.data.storage.client.SeekableInputStream
import logcat.logcat
import org.apache.pdfbox.io.RandomAccessRead
import org.apache.pdfbox.io.RandomAccessReadView

internal class IRandomAccessSourceImpl(private val seekableInputStream: SeekableInputStream) :
    RandomAccessRead {
    override fun length(): Long {
        return seekableInputStream.length().also {
            logcat { "length(): $it" }
        }
    }

    override fun isClosed(): Boolean {
        return seekableInputStream.isClosed().also {
            logcat { "isClosed(): $it" }
        }
    }

    override fun isEOF(): Boolean {
        return seekableInputStream.isEOF().also {
            logcat { "isEOF((): $it" }
        }
    }

    override fun createView(startPosition: Long, streamLength: Long): RandomAccessReadView {
        return RandomAccessReadView(this, startPosition, streamLength).also {
            logcat { "createView(): $it" }
        }
    }

    override fun close() {
        return seekableInputStream.close().also {
            logcat { "close(): $it" }
        }
    }

    override fun read(): Int {
        return seekableInputStream.read().also {
            logcat { "read(): $it" }
        }
    }

    override fun read(b: ByteArray, offset: Int, length: Int): Int {
        return seekableInputStream.read(b, offset, length).also {
            logcat { "read(b): $it" }
        }
    }

    override fun getPosition(): Long {
        return seekableInputStream.position().also {
            logcat { "getPosition(): $it" }
        }
    }

    override fun seek(position: Long) {
        seekableInputStream.seek(position, 0).also {
            logcat { "seek(): $it" }
        }
    }
}
