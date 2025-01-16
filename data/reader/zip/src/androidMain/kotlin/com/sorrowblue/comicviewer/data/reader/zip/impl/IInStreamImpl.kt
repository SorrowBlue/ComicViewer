package com.sorrowblue.comicviewer.data.reader.zip.impl

import com.sorrowblue.comicviewer.data.storage.client.SeekableInputStream
import net.sf.sevenzipjbinding.IInStream

internal class IInStreamImpl(private val seekableInputStream: SeekableInputStream) : IInStream {
    override fun read(data: ByteArray) = seekableInputStream.read(data)
    override fun seek(offset: Long, seekOrigin: Int) = seekableInputStream.seek(offset, seekOrigin)
    override fun close() = seekableInputStream.close()
}
