/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.data.storage.smb.impl

import com.sorrowblue.comicviewer.data.storage.client.FileClient
import com.sorrowblue.comicviewer.data.storage.client.FileClientException
import com.sorrowblue.comicviewer.data.storage.client.FileClientKey
import com.sorrowblue.comicviewer.data.storage.client.FileReaderFactoryMap
import com.sorrowblue.comicviewer.data.storage.client.SeekableInputStream
import com.sorrowblue.comicviewer.data.storage.smb.ntStatusString
import com.sorrowblue.comicviewer.domain.model.bookshelf.SmbServer
import com.sorrowblue.comicviewer.domain.model.common.IoDispatcher
import com.sorrowblue.comicviewer.domain.model.file.BookFile
import com.sorrowblue.comicviewer.domain.model.file.BookFolder
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.model.file.FileAttribute
import com.sorrowblue.comicviewer.domain.model.file.Folder
import com.sorrowblue.comicviewer.domain.model.file.SUPPORTED_IMAGE
import com.sorrowblue.comicviewer.domain.model.file.extension
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import dev.zacsweers.metro.ContributesIntoMap
import java.net.URI
import java.net.URISyntaxException
import java.net.URLDecoder
import java.net.UnknownHostException
import java.util.Properties
import kotlin.io.path.Path
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.io.Source
import kotlinx.io.asSource
import kotlinx.io.buffered
import logcat.LogPriority
import logcat.asLog
import logcat.logcat
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

private data class CachedCifsContext(
    val host: String,
    val port: Int,
    val auth: SmbServer.Auth,
    val context: CIFSContext,
)

private var cachedCifsContext: CachedCifsContext? = null

private val mutex = Mutex()

@AssistedInject
internal actual class SmbFileClient(
    @Assisted bookshelf: SmbServer,
    fileReaderFactoryMap: FileReaderFactoryMap,
    @IoDispatcher dispatcher: CoroutineDispatcher,
) : FileClient<SmbServer>(bookshelf, fileReaderFactoryMap, dispatcher) {
    @ContributesIntoMap(AppScope::class)
    @FileClientKey(SmbServer::class)
    @AssistedFactory
    actual fun interface Factory : FileClient.Factory<SmbServer> {
        actual override fun create(bookshelf: SmbServer): SmbFileClient
    }

    actual override suspend fun source(file: File): Source = runCommand {
        smbFile(file.path).openInputStream().asSource().buffered()
    }

    actual override suspend fun connect(path: String) {
        val exists = runCommand {
            smbFile(path).use {
                it.connect()
                it.exists()
            }
        }
        if (!exists) {
            throw FileClientException.InvalidPath()
        }
    }

    actual override suspend fun exists(path: String): Boolean = runCommand {
        smbFile(path).exists()
    }

    actual override suspend fun current(path: String, resolveImageFolder: Boolean): File =
        runCommand {
            smbFile(path).toFileModel(resolveImageFolder)
        }

    actual override suspend fun attribute(path: String): FileAttribute = runCommand {
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
                volume = hasAttributes(SmbConstants.ATTR_VOLUME),
            )
        }
    }

    actual override suspend fun fileSize(path: String): Long {
        return runCommand {
            val smbFile = smbFile(path)
            if (smbFile.isDirectory) {
                return smbFile.listFiles().sumOf { fileSize(it.url.path) }
            } else {
                smbFile.length()
            }
        }
    }

    private fun SmbFile.hasAttributes(attribute: Int): Boolean =
        attributes and attribute == attribute

    actual override suspend fun listFiles(file: File, resolveImageFolder: Boolean): List<File> =
        runCommand {
            smbFile(file.path)
                .listFiles()
                .map { smbFile -> smbFile.toFileModel(resolveImageFolder) }
        }

    actual override suspend fun seekableInputStream(file: File): SeekableInputStream = runCommand {
        SmbSeekableInputStream(smbFile(file.path))
    }

    private inline fun <R> runCommand(action: () -> R): R = runCatching {
        action()
    }.getOrElse {
        throw when (it) {
            is SmbException -> {
                logcat(
                    LogPriority.INFO,
                ) { "ntStatus=${ntStatusString(it.ntStatus)} ${it.asLog()}" }
                when (it.ntStatus) {
                    NtStatus.NT_STATUS_BAD_NETWORK_NAME -> FileClientException.InvalidPath()

                    NtStatus.NT_STATUS_LOGON_FAILURE -> FileClientException.InvalidAuth()

                    NtStatus.NT_STATUS_INVALID_PARAMETER -> FileClientException.InvalidPath()

                    NtStatus.NT_STATUS_UNSUCCESSFUL -> {
                        if (it.cause is ConnectionTimeoutException ||
                            it.cause is TransportException ||
                            it.cause is UnknownHostException
                        ) {
                            FileClientException.InvalidServer()
                        } else if (it.message ==
                            "IPC signing is enforced, but no signing is available"
                        ) {
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

    private fun SmbFile.toFileModel(resolveImageFolder: Boolean = false): File {
        val parentPath = Path(url.path)
            .parent
            ?.toString()
            .orEmpty()
            .replace("\\", "/")
            .removeSuffix("/") + "/"
        if (resolveImageFolder && isDirectory && runCatching {
                children { it.isFile && it.name.extension in SUPPORTED_IMAGE }.asSequence().take(1)
                    .toList().isNotEmpty()
            }.getOrDefault(false)
        ) {
            return BookFolder(
                path = url.path,
                bookshelfId = this@SmbFileClient.bookshelf.id,
                name = name.removeSuffix("/"),
                parent = parentPath,
                size = -1,
                lastModifier = lastModified,
                isHidden = isHidden,
            )
        }
        return if (isDirectory) {
            Folder(
                path = url.path,
                bookshelfId = this@SmbFileClient.bookshelf.id,
                name = name.removeSuffix("/"),
                parent = parentPath,
                size = -1,
                lastModifier = lastModified,
                isHidden = isHidden,
            )
        } else {
            BookFile(
                path = url.path,
                bookshelfId = this@SmbFileClient.bookshelf.id,
                name = name.removeSuffix("/"),
                parent = parentPath,
                size = length(),
                lastModifier = lastModified,
                isHidden = isHidden,
            )
        }
    }

    @Suppress("UnusedPrivateFunction")
    private fun SmbServer.smbFile(path: String, context: CIFSContext): SmbFile = SmbFile(
        URI(
            "smb",
            null,
            host,
            port,
            path,
            null,
            null,
        ).decode(),
        context,
    )

    private fun CachedCifsContext.isSameServer(server: SmbServer): Boolean =
        host == server.host && port == server.port && auth == server.auth

    @Suppress("RedundantSuspendModifier", "UnusedPrivateFunction")
    private suspend fun smbFile(path: String): SmbFile {
        val context = mutex.withLock {
            cachedCifsContext?.takeIf { it.isSameServer(bookshelf) }?.context
                ?: cifsContext().also {
                    cachedCifsContext = CachedCifsContext(
                        host = bookshelf.host,
                        port = bookshelf.port,
                        auth = bookshelf.auth,
                        context = it,
                    )
                }
        }
        return bookshelf.smbFile(path, context)
    }

    private fun URI.decode() = URLDecoder.decode(toString().replace("+", "%2B"), "UTF-8")

    private fun cifsContext(): CIFSContext {
        val prop = Properties().apply {
            setProperty("jcifs.client.minVersion", DialectVersion.SMB202.name)
            setProperty("jcifs.client.maxVersion", DialectVersion.SMB311.name)
            setProperty("jcifs.client.dfs.disabled", "true")
            setProperty("jcifs.client.connTimeout", "5000")
        }
        val context = BaseContext(PropertyConfiguration(prop))
        return when (val auth = this.bookshelf.auth) {
            SmbServer.Auth.Guest -> context.withGuestCredentials()

            is SmbServer.Auth.UsernamePassword -> context.withCredentials(
                NtlmPasswordAuthenticator(auth.domain, auth.username, auth.password),
            )
        }
    }
}
