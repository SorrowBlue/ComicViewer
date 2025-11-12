package com.sorrowblue.comicviewer.data.storage.smb.impl

import com.sorrowblue.comicviewer.data.storage.client.SeekableInputStream
import org.codelibs.jcifs.smb.SmbConstants
import org.codelibs.jcifs.smb.impl.SmbFile

internal class SmbSeekableInputStream(smbFile: SmbFile, write: Boolean) : SeekableInputStream {
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
            SeekableInputStream.SEEK_SET -> file.seek(offset)
            SeekableInputStream.SEEK_CUR -> file.seek(file.filePointer + offset)
            SeekableInputStream.SEEK_END -> file.seek(file.length() + offset)
        }
        return file.filePointer
    }

    override fun position(): Long = file.filePointer

    override fun read(buf: ByteArray): Int = file.read(buf)

    override fun close() {
        file.close()
    }
}
