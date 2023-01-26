package com.sorrowblue.comicviewer.data.remote.client.device

import android.content.Context
import android.content.Intent
import android.os.ParcelFileDescriptor
import androidx.core.net.toUri
import androidx.documentfile.provider.DocumentFile
import com.sorrowblue.comicviewer.data.common.DeviceStorageModel
import com.sorrowblue.comicviewer.data.common.FileModel
import com.sorrowblue.comicviewer.data.common.SUPPORTED_IMAGE
import com.sorrowblue.comicviewer.data.common.extension
import com.sorrowblue.comicviewer.data.remote.client.FileClient
import com.sorrowblue.comicviewer.data.remote.client.FileClientException
import com.sorrowblue.comicviewer.data.remote.reader.SeekableInputStream
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.InputStream

internal class DeviceFileClient @AssistedInject constructor(
    @Assisted override val serverModel: DeviceStorageModel,
    @ApplicationContext private val context: Context
) : FileClient {

    @AssistedFactory
    interface Factory : FileClient.Factory<DeviceStorageModel> {
        override fun create(serverModel: DeviceStorageModel): DeviceFileClient
    }

    private val contentResolver = context.contentResolver

    override suspend fun inputStream(fileModel: FileModel): InputStream {
        return kotlin.runCatching {
            ParcelFileDescriptor.AutoCloseInputStream(
                contentResolver.openFileDescriptor(fileModel.uri, "r")
            )
        }.getOrElse {
            it.printStackTrace()
            when (it) {
                is SecurityException -> throw FileClientException.InvalidAuth
                is IllegalArgumentException -> throw FileClientException.InvalidPath
                else -> throw it
            }
        }
    }

    override suspend fun connect(path: String) {
        kotlin.runCatching {
            val takeFlags: Int =
                Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
            contentResolver.takePersistableUriPermission(path.toUri(), takeFlags)
            documentFile(path).exists()
        }.fold({
            if (!it) {
                throw FileClientException.InvalidPath
            }
        }) {
            it.printStackTrace()
            when (it) {
                is SecurityException -> throw FileClientException.InvalidAuth
                is IllegalArgumentException -> throw FileClientException.InvalidPath
                else -> throw it
            }
        }
    }

    override suspend fun exists(fileModel: FileModel): Boolean {
        return kotlin.runCatching {
            fileModel.documentFile.exists()
        }.getOrElse {
            it.printStackTrace()
            when (it) {
                is SecurityException -> throw FileClientException.InvalidAuth
                is IllegalArgumentException -> throw FileClientException.InvalidPath
                else -> throw it
            }
        }
    }

    override suspend fun exists(path: String): Boolean {
        return kotlin.runCatching {
            documentFile(path).exists()
        }.getOrElse {
            it.printStackTrace()
            when (it) {
                is SecurityException -> throw FileClientException.InvalidAuth
                is IllegalArgumentException -> throw FileClientException.InvalidPath
                else -> throw it
            }
        }
    }

    override suspend fun current(path: String): FileModel {
        return kotlin.runCatching {
            documentFile(path).toFileModel()
        }.getOrElse {
            it.printStackTrace()
            when (it) {
                is SecurityException -> throw FileClientException.InvalidAuth
                is IllegalArgumentException -> throw FileClientException.InvalidPath
                else -> throw it
            }
        }
    }

    override suspend fun current(fileModel: FileModel): FileModel {
        return kotlin.runCatching {
            fileModel.documentFile.toFileModel()
        }.getOrElse {
            it.printStackTrace()
            when (it) {
                is SecurityException -> throw FileClientException.InvalidAuth
                is IllegalArgumentException -> throw FileClientException.InvalidPath
                else -> throw it
            }
        }
    }

    override suspend fun listFiles(
        fileModel: FileModel,
        resolveImageFolder: Boolean
    ): List<FileModel> {
        return kotlin.runCatching {
            fileModel.documentFile.listFiles().map { it.toFileModel(resolveImageFolder) }
        }.getOrElse {
            it.printStackTrace()
            when (it) {
                is SecurityException -> throw FileClientException.InvalidAuth
                is IllegalArgumentException -> throw FileClientException.InvalidPath
                else -> throw it
            }
        }
    }

    override suspend fun seekableInputStream(fileModel: FileModel): com.sorrowblue.comicviewer.data.remote.reader.SeekableInputStream {
        return kotlin.runCatching {
            DeviceSeekableInputStream(context, fileModel.uri)
        }.getOrElse {
            it.printStackTrace()
            when (it) {
                is SecurityException -> throw FileClientException.InvalidAuth
                is IllegalArgumentException -> throw FileClientException.InvalidPath
                else -> throw it
            }
        }
    }

    private fun DocumentFile.toFileModel(resolveImageFolder: Boolean = false): FileModel {
        return if (resolveImageFolder && listFiles().any { it.name.orEmpty().extension in SUPPORTED_IMAGE }) {
            FileModel.ImageFolder(
                path = uri.toString(),
                serverModelId = serverModel.id,
                name = name?.removeSuffix("/").orEmpty(),
                parent = parentFile?.uri?.toString().orEmpty(),
                size = length(),
                lastModifier = lastModified(),
                sortIndex = 0,
                cacheKey = "",
                totalPageCount = 0,
                lastReadPage = 0,
                lastRead = 0
            )
        } else if (isFile) {
            FileModel.File(
                path = uri.toString(),
                serverModelId = serverModel.id,
                name = name?.removeSuffix("/").orEmpty(),
                parent = parentFile?.uri?.toString().orEmpty(),
                size = length(),
                lastModifier = lastModified(),
                sortIndex = 0,
                cacheKey = "",
                totalPageCount = 0,
                lastReadPage = 0,
                lastRead = 0
            )
        } else {
            FileModel.Folder(
                path = uri.toString(),
                serverModelId = serverModel.id,
                name = name?.removeSuffix("/").orEmpty(),
                parent = parentFile?.uri?.toString().orEmpty(),
                size = length(),
                lastModifier = lastModified(),
                sortIndex = 0
            )
        }
    }

    private val FileModel.uri get() = path.toUri()

    private fun documentFile(path: String): DocumentFile =
        DocumentFile.fromTreeUri(context, path.toUri())!!

    private val FileModel.documentFile: DocumentFile
        get() = DocumentFile.fromTreeUri(
            context,
            uri
        )!!
}
