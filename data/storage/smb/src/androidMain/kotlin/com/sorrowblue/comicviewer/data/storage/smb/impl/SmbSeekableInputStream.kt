package com.sorrowblue.comicviewer.data.storage.smb.impl

import com.sorrowblue.comicviewer.data.storage.client.SeekableInputStream
import jcifs.SmbConstants
import jcifs.smb.SmbFile
import logcat.logcat

internal class SmbSeekableInputStream(smbFile: SmbFile, write: Boolean) :
    SeekableInputStream {

    private val file = runCatching {
        if (write) {
            smbFile.openRandomAccess("rw", SmbConstants.DEFAULT_SHARING)
        } else {
            smbFile.openRandomAccess("r", SmbConstants.DEFAULT_SHARING)
        }
    }.onFailure {
        it.printStackTrace()
    }.getOrThrow()

    override fun seek(offset: Long, whence: Int): Long {
        when (whence) {
            SeekableInputStream.Companion.SEEK_SET -> file.seek(offset)
            SeekableInputStream.Companion.SEEK_CUR -> file.seek(file.filePointer + offset)
            SeekableInputStream.Companion.SEEK_END -> file.seek(file.length() + offset)
        }
        return file.filePointer
    }

    override fun position(): Long {
        return file.filePointer
    }

    override fun read(buf: ByteArray): Int {
        logcat { "read($buf)" }
        return file.read(buf).also {
            logcat { "read($buf) = $it" }
        }
    }

    override fun close() {
        file.close()
    }
}
