package com.sorrowblue.comicviewer.data.reader.document

import com.sorrowblue.comicviewer.data.storage.client.SeekableInputStream
import com.sorrowblue.comicviewer.plugin.pdf.ISeekableInputStream

internal class RandomAccessReadImpl(private val seekableInputStream: SeekableInputStream) :
    ISeekableInputStream {
    override fun read(buf: ByteArray): Int = seekableInputStream.read(buf)

    override fun seek(offset: Long, whence: Int): Long = seekableInputStream.seek(offset, whence)

    override fun position(): Long = seekableInputStream.position()

    override fun close() {
        seekableInputStream.close()
    }
}
