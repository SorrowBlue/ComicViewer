package com.sorrowblue.comicviewer.data.remote.client.smb

import com.sorrowblue.comicviewer.data.common.FileModel
import com.sorrowblue.comicviewer.data.common.SUPPORTED_IMAGE
import com.sorrowblue.comicviewer.data.common.SmbServerModel
import com.sorrowblue.comicviewer.data.common.extension
import com.sorrowblue.comicviewer.data.remote.client.FileClient
import com.sorrowblue.comicviewer.data.remote.client.SeekableInputStream
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import java.io.InputStream
import java.net.URI
import java.net.URLDecoder
import java.net.URLEncoder
import java.util.Properties
import jcifs.CIFSContext
import jcifs.DialectVersion
import jcifs.config.PropertyConfiguration
import jcifs.context.BaseContext
import jcifs.smb.NtlmPasswordAuthenticator
import jcifs.smb.SmbFile
import jcifs.smb.SmbFileInputStream
import kotlin.io.path.Path
import logcat.logcat

internal class SmbFileClient @AssistedInject constructor(
    @Assisted override val serverModel: SmbServerModel,
) : FileClient {

    @AssistedFactory
    interface Factory : FileClient.Factory<SmbServerModel> {
        override fun create(serverModel: SmbServerModel): SmbFileClient
    }

    override suspend fun inputStream(fileModel: FileModel): InputStream {
        return SmbFileInputStream(fileModel.uri, cifsContext())
    }

    override suspend fun exists(fileModel: FileModel): Boolean {
        return fileModel.smbFile.use { it.exists() }
    }

    override suspend fun exists(path: String): Boolean {
        return smbFile(path).use { it.exists() }
    }

    override suspend fun current(path: String): FileModel {
        return smbFile(path).use { it.toFileModel() }
    }

    override suspend fun current(fileModel: FileModel): FileModel {
        return fileModel.smbFile.use { it.toFileModel() }
    }

    override suspend fun listFiles(fileModel: FileModel, resolveImageFolder: Boolean): List<FileModel> {
        return fileModel.smbFile.use(SmbFile::listFiles).map { it.use { it.toFileModel(resolveImageFolder) } }
    }

    override suspend fun seekableInputStream(fileModel: FileModel): SeekableInputStream {
        return SmbSeekableInputStream(fileModel.uri, cifsContext(), false)
    }

    private fun SmbFile.toFileModel(resolveImageFolder: Boolean = false): FileModel {
        if (resolveImageFolder && isDirectory && listFiles().any { it.name.extension in SUPPORTED_IMAGE }) {
            return FileModel.ImageFolder(
                path = url.path,
                serverModelId = serverModel.id,
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
                serverModelId = serverModel.id,
                name = name.removeSuffix("/"),
                parent = Path(url.path).parent.toString() + "/",
                size = length(),
                lastModifier = lastModified,
                sortIndex = 0
            )
        } else {
            FileModel.File(
                path = url.path,
                serverModelId = serverModel.id,
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
        SmbFile(URI("smb", serverModel.host, path, null).decode(), cifsContext())

    private val FileModel.uri
        get() = URI("smb", serverModel.host, path, null).decode()

    private fun URI.decode() = URLDecoder.decode(toString().replace("+", "%2B"), "UTF-8")

    private fun cifsContext(): CIFSContext {
        val prop = Properties().apply {
            setProperty("jcifs.smb.client.minVersion", DialectVersion.SMB202.name)
            setProperty("jcifs.smb.client.maxVersion", DialectVersion.SMB300.name)
            setProperty("jcifs.smb.client.responseTimeout", "10000")
            setProperty("jcifs.smb.client.soTimeout", "10000")
            setProperty("jcifs.smb.client.connTimeout", "10000")
            setProperty("jcifs.smb.client.sessionTimeout", "10000")
            setProperty("jcifs.smb.client.dfs.disabled", "true")
            setProperty("jcifs.resolveOrder", "DNS")
        }
        val context = BaseContext(PropertyConfiguration(prop))
        return when (val auth = serverModel.auth) {
            SmbServerModel.Guest -> context.withAnonymousCredentials()
            is SmbServerModel.UsernamePassword ->
                context.withCredentials(NtlmPasswordAuthenticator(auth.username, auth.password))
        }
    }
}
