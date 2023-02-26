package com.sorrowblue.comicviewer.data.remote.client.smb

import com.sorrowblue.comicviewer.data.common.FileModel
import com.sorrowblue.comicviewer.data.common.SUPPORTED_IMAGE
import com.sorrowblue.comicviewer.data.common.bookshelf.BookshelfModel
import com.sorrowblue.comicviewer.data.common.extension
import com.sorrowblue.comicviewer.data.remote.client.FileClient
import com.sorrowblue.comicviewer.data.remote.client.FileClientException
import com.sorrowblue.comicviewer.data.remote.reader.SeekableInputStream
import com.sorrowblue.comicviewer.framework.Result
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import java.io.InputStream
import java.net.ConnectException
import java.net.URI
import java.net.URLDecoder
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
    @Assisted override val bookshelfModel: BookshelfModel.SmbServer,
) : FileClient {

    @AssistedFactory
    interface Factory : FileClient.Factory<BookshelfModel.SmbServer> {
        override fun create(bookshelfModel: BookshelfModel.SmbServer): SmbFileClient
    }

    override suspend fun inputStream(fileModel: FileModel): InputStream {
        return SmbFileInputStream(fileModel.uri, cifsContext())
    }

    override suspend fun connect(path: String) {
        kotlin.runCatching {
            smbFile(path).use { it.exists() }
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
                    if (it.cause is TransportException && it.cause!!.cause is ConnectException) {
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

    override suspend fun exists(fileModel: FileModel): Boolean {
        return kotlin.runCatching {
            fileModel.smbFile.use { it.exists() }
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

    override suspend fun current(path: String): FileModel {
        return kotlin.runCatching {
            smbFile(path).use { it.toFileModel() }
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

    override suspend fun current(fileModel: FileModel): FileModel {
        return kotlin.runCatching {
            fileModel.smbFile.use { it.toFileModel() }
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
        fileModel: FileModel,
        resolveImageFolder: Boolean
    ): List<FileModel> {
        return kotlin.runCatching {
            fileModel.smbFile.use(SmbFile::listFiles)
                .map { smbFile -> smbFile.use { it.toFileModel(resolveImageFolder) } }
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

    override suspend fun seekableInputStream(fileModel: FileModel): SeekableInputStream {
        return SmbSeekableInputStream(fileModel.uri, cifsContext(), false)
    }

    private fun SmbFile.toFileModel(resolveImageFolder: Boolean = false): FileModel {
        if (resolveImageFolder && isDirectory && listFiles().any { it.name.extension in SUPPORTED_IMAGE }) {
            return FileModel.ImageFolder(
                path = url.path,
                bookshelfModelId = bookshelfModel.id,
                name = name.removeSuffix("/"),
                parent = Path(url.path).parent.toString() + "/",
                size = length(),
                lastModifier = lastModified,
                sortIndex = 0,
                cacheKey = "",
                totalPageCount = 0,
                lastReadPage = 0,
                lastRead = 0
            )
        }
        return if (isDirectory) {
            FileModel.Folder(
                path = url.path,
                bookshelfModelId = bookshelfModel.id,
                name = name.removeSuffix("/"),
                parent = Path(url.path).parent.toString() + "/",
                size = length(),
                lastModifier = lastModified,
                sortIndex = 0
            )
        } else {
            FileModel.File(
                path = url.path,
                bookshelfModelId = bookshelfModel.id,
                name = name.removeSuffix("/"),
                parent = Path(url.path).parent.toString() + "/",
                size = length(),
                lastModifier = lastModified,
                sortIndex = 0,
                cacheKey = "",
                totalPageCount = 0,
                lastReadPage = 0,
                lastRead = 0
            )
        }
    }

    private val FileModel.smbFile get() = SmbFile(uri, cifsContext())

    private fun smbFile(path: String) =
        SmbFile(
            URI("smb", null, bookshelfModel.host, bookshelfModel.port, path, null, null).decode(),
            cifsContext()
        )

    private val FileModel.uri
        get() = URI("smb", null, bookshelfModel.host, bookshelfModel.port, path, null, null).decode()

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
