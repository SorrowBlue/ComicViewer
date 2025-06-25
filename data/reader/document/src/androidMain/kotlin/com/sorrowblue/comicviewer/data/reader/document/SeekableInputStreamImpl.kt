package com.sorrowblue.comicviewer.data.reader.document

import com.sorrowblue.comicviewer.data.storage.client.SeekableInputStream
import logcat.logcat

internal class SeekableInputStreamImpl(private val seekableInputStream: SeekableInputStream) :
    com.artifex.mupdf.fitz.SeekableInputStream {

    override fun seek(offset: Long, whence: Int): Long {
        return seekableInputStream.seek(offset, whence).also {
            logcat { "seek($offset, $whence) = $it" }
        }
    }

    override fun position(): Long {
        return seekableInputStream.position().also {
            logcat { "position() = $it" }
        }
    }

    override fun read(buf: ByteArray): Int {
        return seekableInputStream.read(buf).also {
            logcat { "read($buf) = $it" }
        }
    }
}
