package com.sorrowblue.comicviewer.data.storage.smb

import com.sorrowblue.comicviewer.data.common.bookshelf.BookshelfModel
import com.sorrowblue.comicviewer.data.reader.SeekableInputStream
import java.net.URI
import java.net.URLDecoder
import java.util.Properties
import jcifs.CIFSContext
import jcifs.DialectVersion
import jcifs.SmbConstants
import jcifs.config.PropertyConfiguration
import jcifs.context.BaseContext
import jcifs.smb.NtlmPasswordAuthenticator
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

internal class SmbSeekableInputStream2(
    private val bookshelfModel: BookshelfModel.SmbServer,
    path: String
) :
    SeekableInputStream {

    interface Factory : SeekableInputStream.Factory<BookshelfModel.SmbServer> {
        override fun create(
            bookshelfModel: BookshelfModel.SmbServer,
            path: String
        ): SmbSeekableInputStream2
    }

    private var file = kotlin.runCatching {
        SmbRandomAccessFile(path.uri, "r", SmbConstants.DEFAULT_SHARING, cifsContext())
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

    private val String.uri
        get() = URI(
            "smb",
            null,
            bookshelfModel.host,
            bookshelfModel.port,
            this,
            null,
            null
        ).decode()

    private fun URI.decode() = URLDecoder.decode(toString().replace("+", "%2B"), "UTF-8")
    private fun cifsContext(): CIFSContext {
        val prop = Properties().apply {
            setProperty("jcifs.smb.client.minVersion", DialectVersion.SMB202.name)
            setProperty("jcifs.smb.client.maxVersion", DialectVersion.SMB300.name)
            setProperty("jcifs.smb.client.responseTimeout", "10000")
            setProperty("jcifs.smb.client.soTimeout", "35000")
            setProperty("jcifs.smb.client.connTimeout", "10000")
            setProperty("jcifs.smb.client.sessionTimeout", "35000")
            setProperty("jcifs.smb.client.dfs.disabled", "true")
            setProperty("jcifs.resolveOrder", "DNS")
        }
        val context = BaseContext(PropertyConfiguration(prop))
        return when (val auth = bookshelfModel.auth) {
            BookshelfModel.SmbServer.Guest -> context.withGuestCrendentials()
            is BookshelfModel.SmbServer.UsernamePassword ->
                context.withCredentials(NtlmPasswordAuthenticator(auth.username, auth.password))
        }
    }
}
