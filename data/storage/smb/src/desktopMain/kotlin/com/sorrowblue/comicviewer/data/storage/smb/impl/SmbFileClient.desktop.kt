package com.sorrowblue.comicviewer.data.storage.smb.impl

import com.sorrowblue.comicviewer.data.storage.client.FileClient
import com.sorrowblue.comicviewer.data.storage.client.FileClientException
import com.sorrowblue.comicviewer.data.storage.client.SeekableInputStream
import com.sorrowblue.comicviewer.data.storage.smb.ntStatusString
import com.sorrowblue.comicviewer.domain.model.SUPPORTED_IMAGE
import com.sorrowblue.comicviewer.domain.model.bookshelf.SmbServer
import com.sorrowblue.comicviewer.domain.model.extension
import com.sorrowblue.comicviewer.domain.model.file.BookFile
import com.sorrowblue.comicviewer.domain.model.file.BookFolder
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.model.file.FileAttribute
import com.sorrowblue.comicviewer.domain.model.file.Folder
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import java.net.URI
import java.net.URISyntaxException
import java.net.URLDecoder
import java.net.UnknownHostException
import java.util.Properties
import kotlin.io.path.Path
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import logcat.LogPriority
import logcat.asLog
import logcat.logcat
import okio.BufferedSource
import okio.buffer
import okio.source
import org.codelibs.jcifs.smb.CIFSContext
import org.codelibs.jcifs.smb.DialectVersion
import org.codelibs.jcifs.smb.SmbConstants
import org.codelibs.jcifs.smb.config.PropertyConfiguration
import org.codelibs.jcifs.smb.context.BaseContext
import org.codelibs.jcifs.smb.impl.NtStatus
import org.codelibs.jcifs.smb.impl.NtlmPasswordAuthenticator
import org.codelibs.jcifs.smb.impl.SmbException
import org.codelibs.jcifs.smb.impl.SmbFile
import org.codelibs.jcifs.smb.util.transport.ConnectionTimeoutException
import org.codelibs.jcifs.smb.util.transport.TransportException

private var rootSmbFile: SmbFile? = null

private val mutex = Mutex()

@AssistedInject
internal actual class SmbFileClient(@Assisted actual override val bookshelf: SmbServer) :
    FileClient<SmbServer> {

    @AssistedFactory
    actual interface Factory : FileClient.Factory<SmbServer> {
        actual override fun create(bookshelf: SmbServer): SmbFileClient
    }

    actual override suspend fun bufferedSource(file: File): BufferedSource {
        return runCommand {
            smbFile(file.path).openInputStream().source().buffer()
        }
    }

    actual override suspend fun connect(path: String) {
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

    actual override suspend fun exists(path: String): Boolean {
        return runCommand {
            smbFile(path).exists()
        }
    }

    actual override suspend fun current(path: String, resolveImageFolder: Boolean): File {
        return runCommand {
            smbFile(path).toFileModel(resolveImageFolder)
        }
    }

    actual override suspend fun attribute(path: String): FileAttribute {
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

    actual override suspend fun listFiles(file: File, resolveImageFolder: Boolean): List<File> {
        return runCommand {
            smbFile(file.path).listFiles()
                .map { smbFile -> smbFile.toFileModel(resolveImageFolder) }
        }
    }

    actual override suspend fun seekableInputStream(file: File): SeekableInputStream {
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
                listFiles { it.isFile && it.name.extension in SUPPORTED_IMAGE }.isNotEmpty()
            }.getOrDefault(
                false
            )
        ) {
            return BookFolder(
                path = url.path,
                bookshelfId = this@SmbFileClient.bookshelf.id,
                name = name.removeSuffix("/"),
                parent = Path(url.path).parent.toString().replace("\\", "/") + "/",
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
                parent = Path(url.path).parent?.toString().orEmpty().replace("\\", "/")
                    .removeSuffix("/") + "/",
                size = 0,
                lastModifier = lastModified,
                isHidden = isHidden,
            )
        } else {
            BookFile(
                path = url.path,
                bookshelfId = this@SmbFileClient.bookshelf.id,
                name = name.removeSuffix("/"),
                parent = Path(url.path).parent?.toString().orEmpty().replace("\\", "/")
                    .removeSuffix("/") + "/",
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
        return sameAuth &&
            server == this@SmbFileClient.bookshelf.host &&
            share == this@SmbFileClient.bookshelf.smbFile(path).share &&
            url.port == this@SmbFileClient.bookshelf.port
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
            SmbServer.Auth.Guest -> context.withGuestCredentials()
            is SmbServer.Auth.UsernamePassword -> context.withCredentials(
                NtlmPasswordAuthenticator(auth.domain, auth.username, auth.password)
            )
        }
    }
}
