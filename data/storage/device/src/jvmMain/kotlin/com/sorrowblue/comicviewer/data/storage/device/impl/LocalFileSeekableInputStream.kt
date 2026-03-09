package com.sorrowblue.comicviewer.data.storage.device.impl

import com.sorrowblue.comicviewer.data.storage.client.SeekableInputStream
import java.io.RandomAccessFile
import java.nio.file.Path

internal class LocalFileSeekableInputStream(path: Path) : SeekableInputStream {
    private val file = RandomAccessFile(path.toFile(), "r")

    override fun read(buf: ByteArray): Int = file.read(buf)

    override fun seek(offset: Long, whence: Int): Long {
        when (whence) {
            SeekableInputStream.Companion.SEEK_SET -> file.seek(offset)
            SeekableInputStream.Companion.SEEK_CUR -> file.seek(file.filePointer + offset)
            SeekableInputStream.Companion.SEEK_END -> file.seek(file.length() + offset)
        }
        return file.filePointer
    }

    override fun position(): Long = file.filePointer

    private var isClosed = false

    override fun close() {
        isClosed = true
        file.close()
    }
}
