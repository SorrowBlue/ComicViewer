package com.sorrowblue.comicviewer.data.remote.client.smb

import com.sorrowblue.comicviewer.data.remote.reader.SeekableInputStream
import jcifs.CIFSContext
import jcifs.SmbConstants
import jcifs.smb.SmbRandomAccessFile

internal class SmbSeekableInputStream(uri: String, tc: CIFSContext, write: Boolean) :
    SeekableInputStream {

    private var file = kotlin.runCatching {
        if (write) SmbRandomAccessFile(uri, "rw", SmbConstants.DEFAULT_SHARING, tc)
        else SmbRandomAccessFile(uri, "r", SmbConstants.DEFAULT_SHARING, tc)
    }.onFailure {
        it.printStackTrace()
    }.getOrThrow()

    override fun seek(offset: Long, whence: Int): Long {
        when (whence) {
            SeekableInputStream.SEEK_SET -> file.seek(offset)
            SeekableInputStream.SEEK_CUR -> file.seek(file.filePointer + offset)
            SeekableInputStream.SEEK_END -> file.seek(file.length() + offset)
        }
        return file.filePointer
    }

    override fun position(): Long {
        return file.filePointer
    }

    override fun read(buf: ByteArray): Int {
        return file.read(buf)
    }

    override fun close() {
        file.close()
    }
}
