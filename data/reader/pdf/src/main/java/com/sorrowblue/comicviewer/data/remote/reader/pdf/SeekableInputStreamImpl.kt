package com.sorrowblue.comicviewer.data.remote.reader.pdf

import com.sorrowblue.comicviewer.data.remote.reader.SeekableInputStream

internal class SeekableInputStreamImpl(private val seekableInputStream: SeekableInputStream) :
    com.artifex.mupdf.fitz.SeekableInputStream {

    override fun seek(p0: Long, p1: Int) = seekableInputStream.seek(p0, p1)

    override fun position() = seekableInputStream.position()

    override fun read(p0: ByteArray) = seekableInputStream.read(p0)
}
