package com.sorrowblue.comicviewer.data.storage.smb.impl

import com.sorrowblue.comicviewer.data.storage.client.FileClient
import com.sorrowblue.comicviewer.data.storage.client.FileClientException
import com.sorrowblue.comicviewer.data.storage.client.SeekableInputStream
import com.sorrowblue.comicviewer.data.storage.client.qualifier.SmbFileClient
import com.sorrowblue.comicviewer.data.storage.smb.ntStatusString
import com.sorrowblue.comicviewer.domain.model.SUPPORTED_IMAGE
import com.sorrowblue.comicviewer.domain.model.bookshelf.SmbServer
import com.sorrowblue.comicviewer.domain.model.extension
import com.sorrowblue.comicviewer.domain.model.file.BookFile
import com.sorrowblue.comicviewer.domain.model.file.BookFolder
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.model.file.FileAttribute
import com.sorrowblue.comicviewer.domain.model.file.Folder
import java.net.URI
import java.net.URISyntaxException
import java.net.URLDecoder
import java.net.UnknownHostException
import java.util.Properties
import jcifs.CIFSContext
import jcifs.DialectVersion
import jcifs.SmbConstants
import jcifs.config.PropertyConfiguration
import jcifs.context.BaseContext
import jcifs.smb.NtStatus
import jcifs.smb.NtlmPasswordAuthenticator
import jcifs.smb.SmbException
import jcifs.smb.SmbFile
import jcifs.smb.SmbFileFilter
import jcifs.util.transport.ConnectionTimeoutException
import jcifs.util.transport.TransportException
import kotlin.io.path.Path
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import logcat.LogPriority
import logcat.asLog
import logcat.logcat
import okio.BufferedSource
import okio.buffer
import okio.source
import org.koin.core.annotation.Factory
import org.koin.core.annotation.InjectedParam

private var rootSmbFile: SmbFile? = null
private val mutex = Mutex()

@Factory
@SmbFileClient
internal actual class SmbFileClient(
    @InjectedParam override val bookshelf: SmbServer,
) : FileClient<SmbServer> {

    override suspend fun bufferedSource(file: File): BufferedSource {
        return runCommand {
            smbFile(file.path).openInputStream().source().buffer()
        }
    }

    override suspend fun connect(path: String) {
        kotlin.runCatching {
            smbFile(path).use {
                it.connect()
                it.exists()
            }
        }.fold({
            if (!it) {
                throw FileClientException.InvalidPath()
            }
        }) {
            when (it) {
                is SmbException -> {
                    logcat(LogPriority.INFO) { "ntStatus=${ntStatusString(it.ntStatus)} ${it.asLog()}" }
                    when (it.ntStatus) {
                        NtStatus.NT_STATUS_BAD_NETWORK_NAME -> throw FileClientException.InvalidPath()
                        NtStatus.NT_STATUS_LOGON_FAILURE -> throw FileClientException.InvalidAuth()
                        NtStatus.NT_STATUS_INVALID_PARAMETER -> throw FileClientException.InvalidPath()
                        NtStatus.NT_STATUS_UNSUCCESSFUL -> {
                            if (it.cause is ConnectionTimeoutException || it.cause is TransportException || it.cause is UnknownHostException) {
                                throw FileClientException.InvalidServer()
                            } else if (it.message == "IPC signing is enforced, but no signing is available") {
                                throw FileClientException.InvalidAuth()
                            } else {
                                throw it
                            }
                        }

                        else -> throw it
                    }
                }

                is URISyntaxException -> throw FileClientException.InvalidPath()

                else -> {
                    logcat(LogPriority.INFO) { it.asLog() }
                    throw it
                }
            }
        }
    }

    override suspend fun exists(path: String): Boolean {
        return runCommand {
            smbFile(path).exists()
        }
    }

    override suspend fun current(path: String, resolveImageFolder: Boolean): File {
        return runCommand {
            smbFile(path).toFileModel(resolveImageFolder)
        }
    }

    override suspend fun attribute(path: String): FileAttribute {
        return runCommand {
            smbFile(path).run {
                FileAttribute(
                    archive = hasAttributes(SmbConstants.ATTR_ARCHIVE),
                    compressed = hasAttributes(SmbConstants.ATTR_COMPRESSED),
                    directory = hasAttributes(SmbConstants.ATTR_DIRECTORY),
                    normal = hasAttributes(SmbConstants.ATTR_NORMAL),
                    readonly = hasAttributes(SmbConstants.ATTR_READONLY),
                    system = hasAttributes(SmbConstants.ATTR_SYSTEM),
                    temporary = hasAttributes(SmbConstants.ATTR_TEMPORARY),
                    sharedRead = hasAttributes(SmbConstants.FILE_SHARE_READ),
                    hidden = hasAttributes(SmbConstants.ATTR_HIDDEN),
                    volume = hasAttributes(SmbConstants.ATTR_VOLUME)
                )
            }
        }
    }

    private fun SmbFile.hasAttributes(attribute: Int): Boolean {
        return attributes and attribute == attribute
    }

    override suspend fun listFiles(
        file: File,
        resolveImageFolder: Boolean,
    ): List<File> {
        return runCommand {
            smbFile(file.path).listFiles()
                .map { smbFile -> smbFile.toFileModel(resolveImageFolder) }
        }
    }

    override suspend fun seekableInputStream(file: File): SeekableInputStream {
        return runCommand {
            SmbSeekableInputStream(smbFile(file.path), false)
        }
    }

    private inline fun <R> runCommand(action: () -> R): R {
        return runCatching {
            action()
        }.getOrElse {
            throw when (it) {
                is SmbException -> {
                    logcat(LogPriority.INFO) { "ntStatus=${ntStatusString(it.ntStatus)} ${it.asLog()}" }
                    when (it.ntStatus) {
                        NtStatus.NT_STATUS_BAD_NETWORK_NAME -> FileClientException.InvalidPath()
                        NtStatus.NT_STATUS_LOGON_FAILURE -> FileClientException.InvalidAuth()
                        NtStatus.NT_STATUS_INVALID_PARAMETER -> FileClientException.InvalidPath()
                        NtStatus.NT_STATUS_UNSUCCESSFUL -> {
                            if (it.cause is ConnectionTimeoutException || it.cause is TransportException) {
                                FileClientException.InvalidServer()
                            } else if (it.message == "IPC signing is enforced, but no signing is available") {
                                FileClientException.InvalidAuth()
                            } else {
                                it
                            }
                        }

                        else -> it
                    }
                }

                is URISyntaxException -> FileClientException.InvalidPath()

                else -> {
                    logcat(LogPriority.INFO) { it.asLog() }
                    it
                }
            }
        }
    }

    private fun SmbFile.toFileModel(resolveImageFolder: Boolean = false): File {
        if (resolveImageFolder && isDirectory && runCatching {
                listFiles(SmbFileFilter { it.isFile && it.name.extension in SUPPORTED_IMAGE }).isNotEmpty()
            }.getOrDefault(
                false
            )
        ) {
            return BookFolder(
                path = url.path,
                bookshelfId = this@SmbFileClient.bookshelf.id,
                name = name.removeSuffix("/"),
                parent = Path(url.path).parent.toString() + "/",
                size = 0,
                lastModifier = lastModified,
                isHidden = isHidden,
            )
        }
        return if (isDirectory) {
            Folder(
                path = url.path,
                bookshelfId = this@SmbFileClient.bookshelf.id,
                name = name.removeSuffix("/"),
                parent = Path(url.path).parent?.toString().orEmpty().removeSuffix("/") + "/",
                size = 0,
                lastModifier = lastModified,
                isHidden = isHidden,
            )
        } else {
            BookFile(
                path = url.path,
                bookshelfId = this@SmbFileClient.bookshelf.id,
                name = name.removeSuffix("/"),
                parent = Path(url.path).parent?.toString().orEmpty().removeSuffix("/") + "/",
                size = length(),
                lastModifier = lastModified,
                isHidden = isHidden,
            )
        }
    }

    private fun SmbServer.smbFile(path: String): SmbFile {
        return SmbFile(
            URI(
                "smb",
                null,
                host,
                port,
                path,
                null,
                null
            ).decode(),
            cifsContext()
        )
    }

    private fun SmbFile.isSame(path: String): Boolean {
        val credentials = context.credentials
        val bookshelfAuth = this@SmbFileClient.bookshelf.auth
        val sameAuth = if (credentials !is NtlmPasswordAuthenticator) {
            false
        } else {
            when (bookshelfAuth) {
                SmbServer.Auth.Guest -> credentials.isGuest
                is SmbServer.Auth.UsernamePassword -> {
                    NtlmPasswordAuthenticator(
                        bookshelfAuth.domain,
                        bookshelfAuth.username,
                        bookshelfAuth.password
                    ) == credentials
                }
            }
        }
        return sameAuth && server == this@SmbFileClient.bookshelf.host && share == this@SmbFileClient.bookshelf.smbFile(
            path
        ).share
    }

    private suspend fun smbFile(path: String): SmbFile {
        return mutex.withLock {
            rootSmbFile?.let { smbFile ->
                if (smbFile.isSame(path)) {
                    val nPath = path.removePrefix("/${smbFile.share}/")
                    if (nPath.isEmpty() || nPath == "/") smbFile else smbFile.resolve(nPath) as SmbFile
                } else {
                    null
                }
            } ?: run {
                val smbFile = this.bookshelf.smbFile(path)
                smbFile.share?.let { share ->
                    this.bookshelf.smbFile("/$share/").let {
                        rootSmbFile = it
                        val nPath = path.removePrefix("/${smbFile.share}/")
                        if (nPath.isEmpty() || nPath == "/") it else it.resolve(nPath) as SmbFile
                    }
                } ?: smbFile.also {
                    rootSmbFile = smbFile
                }
            }
        }
    }

    private fun URI.decode() = URLDecoder.decode(toString().replace("+", "%2B"), "UTF-8")

    private fun cifsContext(): CIFSContext {
        val prop = Properties().apply {
            setProperty("jcifs.smb.client.minVersion", DialectVersion.SMB202.name)
            setProperty("jcifs.smb.client.maxVersion", DialectVersion.SMB311.name)
            setProperty("jcifs.smb.client.dfs.disabled", "true")
            setProperty("jcifs.smb.client.connTimeout", "5000")
        }
        val context = BaseContext(PropertyConfiguration(prop))
        return when (val auth = this.bookshelf.auth) {
            SmbServer.Auth.Guest -> context.withGuestCrendentials()
            is SmbServer.Auth.UsernamePassword -> context.withCredentials(
                NtlmPasswordAuthenticator(auth.domain, auth.username, auth.password)
            )
        }
    }
}

internal class SmbSeekableInputStream(smbFile: SmbFile, write: Boolean) :
    SeekableInputStream {

    private val file = kotlin.runCatching {
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
