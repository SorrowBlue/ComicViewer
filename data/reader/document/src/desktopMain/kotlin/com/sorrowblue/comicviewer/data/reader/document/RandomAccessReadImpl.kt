package com.sorrowblue.comicviewer.data.reader.document

import com.sorrowblue.comicviewer.data.storage.client.SeekableInputStream
import org.apache.pdfbox.io.RandomAccessRead
import org.apache.pdfbox.io.RandomAccessReadView

internal class RandomAccessReadImpl(
    private val seekableInputStream: SeekableInputStream,
) : RandomAccessRead {

    override fun close() {
        seekableInputStream.close()
    }

    override fun read(): Int {
        return seekableInputStream.read()
    }

    override fun read(b: ByteArray, offset: Int, length: Int): Int {
        return seekableInputStream.read(b, offset, length)
    }

    override fun getPosition(): Long {
        return seekableInputStream.position()
    }

    override fun seek(position: Long) {
        seekableInputStream.seek(position, 0)
    }

    override fun length(): Long {
        return seekableInputStream.length()
    }

    override fun isClosed(): Boolean {
        return seekableInputStream.isClosed()
    }

    override fun isEOF(): Boolean {
        return seekableInputStream.isEOF()
    }

    override fun createView(startPosition: Long, streamLength: Long): RandomAccessReadView {
        return RandomAccessReadView(this, startPosition, streamLength)
    }
}
