package com.sorrowblue.comicviewer.data.reader.zip.impl

import com.sorrowblue.comicviewer.data.storage.client.SeekableInputStream

internal class IInStreamImpl(private val seekableInputStream: SeekableInputStream) : com.sorrowblue.kioarch.SeekableSource {

    override fun read(buffer: ByteArray, offset: Int, length: Int): Int {
        return seekableInputStream.read(buffer, offset, length)
    }
    override fun seek(position: Long) {
        seekableInputStream.seek(position)
    }

    override fun position(): Long {
        return seekableInputStream.position()
    }

    override fun length(): Long {
        return seekableInputStream.length()
    }

    override fun close() = seekableInputStream.close()
}
