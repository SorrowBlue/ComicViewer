package com.sorrowblue.comicviewer.data.reader.document

import com.sorrowblue.comicviewer.data.storage.client.SeekableInputStream
import com.sorrowblue.comicviewer.plugin.aidl.ISeekableInputStream

internal class SeekableInputStreamImpl(private val seekableInputStream: SeekableInputStream) :
    ISeekableInputStream.Stub() {

    override fun read(buf: ByteArray) = seekableInputStream.read(buf)

    override fun seek(offset: Long, whence: Int) = seekableInputStream.seek(offset, whence)

    override fun position() = seekableInputStream.position()

    override fun close() = seekableInputStream.close()
}
