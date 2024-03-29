package com.sorrowblue.comicviewer.data.storage.smb

import com.sorrowblue.comicviewer.data.reader.SeekableInputStream
import com.sorrowblue.comicviewer.data.storage.client.FileClient
import com.sorrowblue.comicviewer.data.storage.client.FileClientException
import com.sorrowblue.comicviewer.domain.model.Result
import com.sorrowblue.comicviewer.domain.model.SUPPORTED_IMAGE
import com.sorrowblue.comicviewer.domain.model.bookshelf.SmbServer
import com.sorrowblue.comicviewer.domain.model.extension
import com.sorrowblue.comicviewer.domain.model.file.BookFile
import com.sorrowblue.comicviewer.domain.model.file.BookFolder
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.model.file.Folder
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import java.io.InputStream
import java.net.ConnectException
import java.net.URI
import java.net.URLDecoder
import java.net.UnknownHostException
import java.util.Properties
import jcifs.CIFSContext
import jcifs.DialectVersion
import jcifs.config.PropertyConfiguration
import jcifs.context.BaseContext
import jcifs.smb.NtStatus
import jcifs.smb.NtlmPasswordAuthenticator
import jcifs.smb.SmbAuthException
import jcifs.smb.SmbException
import jcifs.smb.SmbFile
import jcifs.smb.SmbFileInputStream
import jcifs.util.transport.TransportException
import kotlin.io.path.Path
import logcat.LogPriority
import logcat.logcat

internal class SmbFileClient @AssistedInject constructor(
    @Assisted override val bookshelf: SmbServer,
) : FileClient {

    @AssistedFactory
    interface Factory : FileClient.Factory<SmbServer> {
        override fun create(bookshelfModel: SmbServer): SmbFileClient
    }

    override suspend fun inputStream(file: File): InputStream {
        return SmbFileInputStream(file.uri, cifsContext())
    }

    override suspend fun connect(path: String) {
        kotlin.runCatching {
            smbFile(path).use {
                it.connect()
                it.exists()
            }
        }.fold({
            if (it) {
                Result.Success(Unit)
            } else {
                throw FileClientException.InvalidPath
            }
        }) {
            it.printStackTrace()
            when (it) {
                is SmbAuthException -> {
                    logcat(LogPriority.INFO) { "ntStatus=${ntStatusString(it.ntStatus)}" }
                    throw FileClientException.InvalidAuth
                }

                is SmbException -> {
                    if (it.cause is UnknownHostException) {
                        throw FileClientException.NoNetwork
                    } else if (it.cause is TransportException && it.cause!!.cause is ConnectException) {
                        throw FileClientException.NoNetwork
                    } else {
                        logcat(LogPriority.INFO) { "ntStatus=${ntStatusString(it.ntStatus)}" }
                        when (it.ntStatus) {
                            NtStatus.NT_STATUS_BAD_NETWORK_NAME -> throw FileClientException.InvalidPath
                            NtStatus.NT_STATUS_UNSUCCESSFUL -> throw FileClientException.InvalidServer
                            else -> throw it
                        }
                    }
                }

                else -> throw it
            }
        }
    }

    override suspend fun exists(file: File): Boolean {
        return kotlin.runCatching {
            file.smbFile.use { it.exists() }
        }.getOrElse {
            it.printStackTrace()
            when (it) {
                is SmbAuthException -> {
                    logcat(LogPriority.INFO) { "ntStatus=${ntStatusString(it.ntStatus)}" }
                    throw FileClientException.InvalidAuth
                }

                is SmbException -> {
                    if (it.cause is TransportException && it.cause!!.cause is ConnectException) {
                        throw FileClientException.NoNetwork
                    } else {
                        logcat(LogPriority.INFO) { "ntStatus=${ntStatusString(it.ntStatus)}" }
                        when (it.ntStatus) {
                            NtStatus.NT_STATUS_BAD_NETWORK_NAME -> false
                            NtStatus.NT_STATUS_UNSUCCESSFUL -> throw FileClientException.InvalidServer
                            else -> throw it
                        }
                    }
                }

                else -> throw it
            }
        }
    }

    override suspend fun exists(path: String): Boolean {
        return kotlin.runCatching {
            smbFile(path).use { it.exists() }
        }.getOrElse {
            it.printStackTrace()
            when (it) {
                is SmbAuthException -> {
                    logcat(LogPriority.ERROR) { "ntStatus=${ntStatusString(it.ntStatus)}" }
                    throw FileClientException.InvalidAuth
                }

                is SmbException -> {
                    if (it.cause is TransportException && it.cause!!.cause is ConnectException) {
                        throw FileClientException.NoNetwork
                    } else {
                        logcat(LogPriority.ERROR) { "ntStatus=${ntStatusString(it.ntStatus)}" }
                        when (it.ntStatus) {
                            NtStatus.NT_STATUS_BAD_NETWORK_NAME -> false
                            NtStatus.NT_STATUS_UNSUCCESSFUL -> throw FileClientException.InvalidServer
                            else -> throw it
                        }
                    }
                }

                else -> throw it
            }
        }
    }

    override suspend fun current(path: String): File {
        return kotlin.runCatching {
            smbFile(path).use { it.toFileModel() }
        }.getOrElse {
            it.printStackTrace()
            when (it) {
                is SmbAuthException -> {
                    logcat(LogPriority.ERROR) { "ntStatus=${ntStatusString(it.ntStatus)}" }
                    throw FileClientException.InvalidAuth
                }

                is SmbException -> {
                    if (it.cause is TransportException && it.cause!!.cause is ConnectException) {
                        throw FileClientException.NoNetwork
                    } else {
                        logcat(LogPriority.ERROR) { "ntStatus=${ntStatusString(it.ntStatus)}" }
                        when (it.ntStatus) {
                            NtStatus.NT_STATUS_BAD_NETWORK_NAME -> throw FileClientException.InvalidPath
                            NtStatus.NT_STATUS_UNSUCCESSFUL -> throw FileClientException.InvalidServer
                            else -> throw it
                        }
                    }
                }

                else -> throw it
            }
        }
    }

    override suspend fun current(file: File): File {
        return kotlin.runCatching {
            file.smbFile.use { it.toFileModel() }
        }.getOrElse {
            it.printStackTrace()
            when (it) {
                is SmbAuthException -> {
                    logcat(LogPriority.ERROR) { "ntStatus=${ntStatusString(it.ntStatus)}" }
                    throw FileClientException.InvalidAuth
                }

                is SmbException -> {
                    if (it.cause is TransportException && it.cause!!.cause is ConnectException) {
                        throw FileClientException.NoNetwork
                    } else {
                        logcat(LogPriority.ERROR) { "ntStatus=${ntStatusString(it.ntStatus)}" }
                        when (it.ntStatus) {
                            NtStatus.NT_STATUS_BAD_NETWORK_NAME -> throw FileClientException.InvalidPath
                            NtStatus.NT_STATUS_UNSUCCESSFUL -> throw FileClientException.InvalidServer
                            else -> throw it
                        }
                    }
                }

                else -> throw it
            }
        }
    }

    override suspend fun listFiles(
        file: File,
        resolveImageFolder: Boolean,
    ): List<File> {
        return kotlin.runCatching {
            file.smbFile.use(SmbFile::listFiles)
                .map { smbFile -> smbFile.use { it.toFileModel(resolveImageFolder) } }
        }.getOrElse {
            it.printStackTrace()
            when (it) {
                is SmbAuthException -> {
                    logcat(LogPriority.ERROR) { "ntStatus=${ntStatusString(it.ntStatus)}" }
                    throw FileClientException.InvalidAuth
                }

                is SmbException -> {
                    if (it.cause is TransportException && it.cause!!.cause is ConnectException) {
                        throw FileClientException.NoNetwork
                    } else {
                        logcat(LogPriority.ERROR) { "ntStatus=${ntStatusString(it.ntStatus)}" }
                        when (it.ntStatus) {
                            NtStatus.NT_STATUS_BAD_NETWORK_NAME -> throw FileClientException.InvalidPath
                            NtStatus.NT_STATUS_UNSUCCESSFUL -> throw FileClientException.InvalidServer
                            else -> throw it
                        }
                    }
                }

                else -> throw it
            }
        }
    }

    override suspend fun seekableInputStream(file: File): SeekableInputStream {
        return SmbSeekableInputStream(file.uri, cifsContext(), false)
    }

    private fun SmbFile.toFileModel(resolveImageFolder: Boolean = false): File {
        if (resolveImageFolder && isDirectory && listFiles().any { it.name.extension in SUPPORTED_IMAGE }) {
            return BookFolder(
                path = url.path,
                bookshelfId = bookshelf.id,
                name = name.removeSuffix("/"),
                parent = Path(url.path).parent.toString() + "/",
                size = length(),
                lastModifier = lastModified,
                sortIndex = 0,
                cacheKey = "",
                totalPageCount = 0,
                lastPageRead = 0,
                lastReadTime = 0
            )
        }
        return if (isDirectory) {
            Folder(
                path = url.path,
                bookshelfId = bookshelf.id,
                name = name.removeSuffix("/"),
                parent = Path(url.path).parent?.toString().orEmpty().removeSuffix("/") + "/",
                size = length(),
                lastModifier = lastModified,
                sortIndex = 0,
            )
        } else {
            BookFile(
                path = url.path,
                bookshelfId = bookshelf.id,
                name = name.removeSuffix("/"),
                parent = Path(url.path).parent?.toString().orEmpty().removeSuffix("/") + "/",
                size = length(),
                lastModifier = lastModified,
                sortIndex = 0,
                cacheKey = "",
                totalPageCount = 0,
                lastPageRead = 0,
                lastReadTime = 0
            )
        }
    }

    private val File.smbFile get() = SmbFile(uri, cifsContext())

    private fun smbFile(path: String) =
        SmbFile(
            URI("smb", null, bookshelf.host, bookshelf.port, path, null, null).decode(),
            cifsContext()
        )

    private val File.uri
        get() = URI(
            "smb",
            null,
            bookshelf.host,
            bookshelf.port,
            path,
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
        return when (val auth = bookshelf.auth) {
            SmbServer.Auth.Guest -> context.withGuestCrendentials()
            is SmbServer.Auth.UsernamePassword ->
                context.withCredentials(NtlmPasswordAuthenticator(auth.username, auth.password))
        }
    }
}
