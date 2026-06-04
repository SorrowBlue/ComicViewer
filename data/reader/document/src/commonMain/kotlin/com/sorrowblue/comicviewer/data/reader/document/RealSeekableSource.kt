package com.sorrowblue.comicviewer.data.reader.document

import com.sorrowblue.comicviewer.data.storage.client.SeekableInputStream
import com.sorrowblue.kpdfium.SeekableSource

internal class RealSeekableSource(private val seekableInputStream: SeekableInputStream) :
    SeekableSource {

    override fun read(buffer: ByteArray, offset: Int, length: Int): Int =
        seekableInputStream.read(buffer, offset, length)

    override fun seek(position: Long) {
        seekableInputStream.seek(position)
    }

    override fun position(): Long = seekableInputStream.position()

    override fun length(): Long = seekableInputStream.length()

    override fun close() = seekableInputStream.close()
}
